package io.github.benwhitehead.scala.finch

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, RichHttp}
import io.finch._
import io.github.benwhitehead.scala.finch.util.SLF4JLogging

import java.net.InetSocketAddress

/**
 * @author Ben Whitehead
 */
object AccessLogServer {
  lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)
  def main(args: Array[String]) {
    SLF4JLogging {
      val router = Endpoint.join(
        Echo
      ) orElse Endpoint.NotFound

      val port = args(0).toInt
      logger.info(s"Starting server listening on port: $port")
      ServerBuilder()
        .codec(RichHttp[HttpRequest](Http()))
        .bindTo(new InetSocketAddress(port))
        .name("finch-experimentation")
        .build(AccessLog andThen HandleErrors andThen router.toService)
    }
  }
}
