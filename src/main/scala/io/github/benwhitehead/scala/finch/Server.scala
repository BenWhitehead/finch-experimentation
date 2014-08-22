package io.github.benwhitehead.scala.finch

import com.twitter.app.App
import com.twitter.finagle.builder.{Server => TServer, ServerBuilder}
import com.twitter.finagle.http.{Http, HttpMuxer, RichHttp}
import com.twitter.finagle.{HttpServer, ListeningServer}
import com.twitter.server.{Admin, Stats, Lifecycle}
import com.twitter.util.Await
import io.finch._

import java.io.{File, FileOutputStream}
import java.lang.management.ManagementFactory
import java.net.InetSocketAddress

/**
 * @author Ben Whitehead
 */
object Server extends App with Lifecycle with Stats with Admin {
  lazy val logger = org.slf4j.LoggerFactory.getLogger(getClass.getName)

  val pid: String = ManagementFactory.getRuntimeMXBean.getName.split('@').head

  case class Config(pidPath: String = "", adminPort: String = ":9990")

  val config = Config()
  var adminServer: Option[ListeningServer] = None
  var server: Option[TServer] = None

  def writePidFile() {
    val pidFile = new File(config.pidPath)
    val pidFileStream = new FileOutputStream(pidFile)
    pidFileStream.write(pid.getBytes)
    pidFileStream.close()
  }

  def removePidFile() {
    val pidFile = new File(config.pidPath)
    pidFile.delete()
  }

  def start(): Unit = {

    if (!config.pidPath.isEmpty) {
      writePidFile()
    }

    adminServer = Some(HttpServer.serve(config.adminPort, HttpMuxer))
    logger.info(s"admin http server started on port: ${config.adminPort}")

    val router = Endpoint.join(
      Echo
    ) orElse Endpoint.NotFound

    val port = args(0).toInt
    logger.info(s"Starting server listening on port: $port")
    server = Some(
      ServerBuilder()
        .codec(RichHttp[HttpRequest](Http()))
        .bindTo(new InetSocketAddress(port))
        .name("srv-finch")
        .build(StatsFilter andThen HandleErrors andThen router.toService)
    )

    adminServer  map { Await.ready(_) }
  }

  def main() {
    logger.info("process " + pid + " started")
    start()
  }

  def stop() {
    server map { _.close() }
    adminServer map { _.close() }
  }

  onExit {
    stop()
    removePidFile()
  }
}
