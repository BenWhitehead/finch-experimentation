package io.github.benwhitehead.scala.finch

import com.twitter.app.{App => TApp}
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.server.Stats
import com.twitter.util.Future
import io.finch.{HttpResponse, HttpRequest}

object StatsFilter extends SimpleFilter[HttpRequest, HttpResponse] with TApp with Stats {
  val stats = statsReceiver.scope("route")
  def apply(request: HttpRequest, service: Service[HttpRequest, HttpResponse]): Future[HttpResponse] = {
    stats.timeFuture(s"${request.method}/Root/${request.path.stripPrefix("/")}") {
      service(request)
    }
  }
}
