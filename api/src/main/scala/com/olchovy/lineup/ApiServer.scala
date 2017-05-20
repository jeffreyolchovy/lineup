package com.olchovy.lineup

import java.net.InetSocketAddress
import scala.util.control.NonFatal
import com.twitter.finagle.Http
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.Method
import com.twitter.util.Await
import org.slf4j.LoggerFactory

class ApiServer(port: Int) {

  import ApiServer._

  private val service = ExceptionHandlingFilter andThen RoutingService.byMethodAndPathObject {
    case Method.Post -> Root / "lineups" => new LineupsHandler
    case Method.Post -> Root / "fitness" => new FitnessHandler
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

object ApiServer {

  private val log = LoggerFactory.getLogger(getClass)

  def main(args: Array[String]): Unit = {
    val port = 8080
    try {
      new ApiServer(port).run()
    } catch {
      case NonFatal(e) =>
        log.error("Unexpected exception encountered", e)
        sys.exit(1)
    }
  }
}
