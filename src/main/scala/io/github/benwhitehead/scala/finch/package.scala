package io.github.benwhitehead.scala

import com.twitter.finagle.{Service, SimpleFilter}
import io.finch._
import io.finch.json.JsonObject
import io.finch.request.{ParamNotFound, ValidationFailed}
import io.finch.response._

import java.text.SimpleDateFormat
import java.util.Date

package object finch {

  abstract class SimpleEndpoint extends Endpoint[HttpRequest, HttpResponse]

  class NotFoundException extends Exception
  class BadRequest extends Exception
  class ForbiddenException extends Exception

  object HandleErrors extends SimpleFilter[HttpRequest, HttpResponse] {
    lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      service(request) handle {
        case e: NotFoundException  => NotFound()
        case e: BadRequest         => BadRequest()
        case e: ValidationFailed   => BadRequest(JsonObject("message" -> e.getMessage))
        case e: ParamNotFound      => BadRequest(JsonObject("message" -> e.getMessage))
        case e: ForbiddenException => Forbidden()
        case t: Throwable          => logger.error("", t); InternalServerError()
      }
    }
  }

  object AccessLog extends SimpleFilter[HttpRequest, HttpResponse] {
    lazy val common = org.slf4j.LoggerFactory.getLogger("access-log")
    lazy val combined = org.slf4j.LoggerFactory.getLogger("access-log-combined")
    def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]) = {
      if (false && common.isTraceEnabled || combined.isTraceEnabled) {
        service(request) flatMap { case resp =>
          val reqHeaders = request.headers()
          val remoteHost = request.remoteHost
          val identd = "-"
          val user = "-"
          val requestEndTime = new SimpleDateFormat("dd/MM/yyyy:hh:mm:ss Z").format(new Date())
          val reqResource = s"${request.method} ${request.uri} ${request.getProtocolVersion()}"
          val statusCode = resp.statusCode
          val responseBytes = asOpt(resp.headers().get("Content-Length")).getOrElse("-")

          if (common.isInfoEnabled) {
            common.trace(f"""$remoteHost%s $identd%s $user%s [$requestEndTime%s] "$reqResource%s" $statusCode%d $responseBytes%s""")
          }
          if (combined.isInfoEnabled) {
            val referer = asOpt(reqHeaders.get("Referer")).getOrElse("-")
            val userAgent = asOpt(reqHeaders.get("User-Agent")).getOrElse("-")
            combined.trace(f"""$remoteHost%s $identd%s $user%s [$requestEndTime%s] "$reqResource%s" $statusCode%d $responseBytes%s "$referer%s" "$userAgent%s"""")
          }
          resp.toFuture
        }
      } else {
        service(request)
      }
    }
    
    def asOpt(value: String) = if (value == null) None else Some(value)
  }
}
