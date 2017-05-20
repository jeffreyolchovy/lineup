package com.olchovy.lineup

import java.net.InetSocketAddress
import scala.util.control.NonFatal
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.io.StreamIO
import com.twitter.util.{Await, Future}
import org.slf4j.LoggerFactory
import scalatags.Text.all._
import scalatags.Text.tags2.title

class WebAppServer(port: Int) {

  import WebAppServer._

  private val service = ExceptionHandlingFilter andThen RoutingService.byMethodAndPathObject {
    case Method.Get -> Root => new IndexHandler
    case Method.Get -> Root / "assets" / ("scripts" | "styles") / _ => new AssetsHandler
    case Method.Post -> Root / "api" / "lineups" => new LineupsHandler
    case Method.Post -> Root / "api" / "fitness" => new FitnessHandler
  }

  def run(): Unit = {
    val bindAddress = new InetSocketAddress(port)
    val server = Http.serve(bindAddress, service)
    try {
      log.info(s"Server listening at $bindAddress")
      Await.ready(server)
    } catch {
      case e: InterruptedException =>
        log.info("Server interrupted")
        Thread.currentThread.interrupt()
        throw e
    } finally {
      log.info("Server shutting down")
      server.close()
    }
  }
}

object WebAppServer {

  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val port = 8080
    try {
      new WebAppServer(port).run()
    } catch {
      case NonFatal(e) =>
        log.error("Unexpected exception encountered", e)
        sys.exit(1)
    }
  }

  class IndexHandler extends Service[Request, Response] {
    def apply(request: Request) = {
      val response = Response(request.version, Status.Ok)
      response.write(IndexHandler.ResponseHtmlString)
      Future.value(response)
    }
  }

  object IndexHandler {
    val ResponseHtmlString = "<!DOCTYPE html>" + html(
      head(
        title("Lineup Generator"),
        meta(charset := "utf-8"),
        link(href := "/assets/styles/main.css", rel := "stylesheet"),
        link(href := "/assets/styles/token-input.css", rel := "stylesheet")
      ),
      body(
        script(src := "/assets/scripts/zepto.js"),
        script(src := "/assets/scripts/underscore.js"),
        script(src := "/assets/scripts/zepto._.tokeninput.js"),
        script(src := "/assets/scripts/backbone.js"),
        script(src := "/assets/scripts/jade-runtime.js"),
        script(src := "/assets/scripts/templates.js"),
        script(src := "/assets/scripts/main.js"),
        script(
          """
          |LineupGui.app = new LineupGui.App();
          |LineupGui.app.start();
          """.stripMargin.trim
        )
      )
    )
  }

  class AssetsHandler extends Service[Request, Response] {
    def apply(request: Request) = {
      val stream = getClass.getResourceAsStream(request.path)
      val response = Response(request.version, Status.Ok)
      response.withOutputStream(StreamIO.copy(stream, _))
      Future.value(response)
    }
  }
}
