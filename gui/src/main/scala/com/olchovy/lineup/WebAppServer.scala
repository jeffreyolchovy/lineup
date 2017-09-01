package com.olchovy.lineup

import scala.util.control.NonFatal
import com.twitter.finagle.Service
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request, Response, Status}
import com.twitter.io.StreamIO
import com.twitter.util.Future
import org.slf4j.LoggerFactory
import scalatags.Text.all._
import scalatags.Text.tags2.title
import com.olchovy.util.{Args, Server}

case class WebAppServer(port: Int) extends Server {

  import WebAppServer._

  val service = ExceptionHandlingFilter andThen RoutingService.byMethodAndPathObject {
    case Method.Get -> Root => new IndexHandler
    case Method.Get -> Root / "assets" / ("scripts" | "styles") / _ => AssetsHandler
    case Method.Get -> Root / "assets" / "scripts" / "third-party" / _ => AssetsHandler
    case Method.Post -> Root / "api" / "lineups" => new LineupsHandler
  }
}

object WebAppServer {

  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    try {
      val port = Args(args).required("port").toInt
      WebAppServer(port).run()
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
        script(src := "/assets/scripts/third-party/zepto.js"),
        script(src := "/assets/scripts/third-party/underscore.js"),
        script(src := "/assets/scripts/zepto._.tokeninput.js"),
        script(src := "/assets/scripts/third-party/backbone.js"),
        script(src := "/assets/scripts/third-party/jade-runtime.js"),
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

  object AssetsHandler extends Service[Request, Response] {
    def apply(request: Request) = {
      val stream = getClass.getResourceAsStream(request.path)
      val response = Response(request.version, Status.Ok)
      response.withOutputStream(StreamIO.copy(stream, _))
      Future.value(response)
    }
  }
}
