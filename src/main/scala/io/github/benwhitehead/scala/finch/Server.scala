package io.github.benwhitehead.scala.finch

import com.twitter.finagle.Service
import com.twitter.finagle.http.Method
import com.twitter.finagle.http.path.{/, Root, ->}
import com.twitter.util.Future
import io.finch._
import io.finch.response.Ok
import io.github.benwhitehead.finch.{HttpEndpoint, SimpleFinchServer}

/**
 * @author Ben Whitehead
 */
trait Server extends SimpleFinchServer {
  override lazy val serverName = "echo"
  def endpoint = Echo
}

object Echo extends HttpEndpoint {
  def service(echo: String) = new Service[HttpRequest, HttpResponse] {
    def apply(request: HttpRequest): Future[HttpResponse] = {
      Ok(echo).toFuture
    }
  }
  def route = {
    case Method.Get -> Root / "echo" / echo => service(echo)
  }
}

object Main extends Server
