package io.github.benwhitehead.scala.finch

import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, RichHttp}
import io.finch._

import java.net.InetSocketAddress

/**
 * @author Ben Whitehead
 */
object Server extends App {
  lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  private val router = Endpoint.join(
    Echo
  ) orElse Endpoint.NotFound

  private val port = args(0).toInt
  logger.info(s"Starting server listening on port: $port")
  ServerBuilder()
    .codec(RichHttp[HttpRequest](Http()))
    .bindTo(new InetSocketAddress(port))
    .name("finch-experimentation")
    .build(HandleErrors andThen router.toService)
}
