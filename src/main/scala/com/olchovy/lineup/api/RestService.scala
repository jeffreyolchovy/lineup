package com.olchovy.lineup.api

import java.net.InetSocketAddress
import cc.spray.json._
import com.twitter.finagle.{Service, SimpleFilter}
import com.twitter.finagle.builder.{Server, ServerBuilder}
import com.twitter.finagle.http.path._
import com.twitter.finagle.http.service.RoutingService
import com.twitter.finagle.http.{Method, Request, Response, RichHttp, Http}
import org.jboss.netty.buffer.ChannelBuffers.copiedBuffer
import org.jboss.netty.handler.codec.http._
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.util.CharsetUtil.UTF_8
import com.twitter.util.Future
import com.olchovy.lineup.domain._

object RestService {

  import RestJsonProtocol._

  lazy val service = new ExceptionHandler andThen new RoutingService(routes = {
    case request => (request.method, Path(request.path)) match {
      case Method.Post -> Root / "lineups" => new LineupsHandler
      case Method.Post -> Root / "fitness" => new FitnessHandler
    }
  })

  def run() {
    ServerBuilder()
      .codec(RichHttp[Request](Http()))
      .bindTo(new InetSocketAddress(8080))
      .name("RestService")
      .build(service)
  }

  class ExceptionHandler extends SimpleFilter[Request, Response] {
    def apply(request: Request, service: Service[Request, Response]) = {
      service(request) handle {
        case error =>
          val errorResponse = Response(HTTP_1_1, INTERNAL_SERVER_ERROR)
          errorResponse.setContent(copiedBuffer(error.getStackTraceString, UTF_8))
          errorResponse
      }
    }
  }

  class LineupsHandler extends Service[Request, Response] {
    def apply(request: Request) = {
      val input = JsonParser(request.getContentString)
      val (strategy, lineup) = input.convertTo[(Strategy, Lineup)]
      val lineups = strategy.execute(lineup.players).map { lineup ⇒
        (strategy.fitness(lineup), lineup.players.map(_.name))
      }.toSeq.sortWith { (a, b) ⇒ 
        a._1 > b._1
      }.map(_._2)
      val output = Map("lineups" → lineups).toJson
      val response = Response()
      response.setContent(copiedBuffer(output.compactPrint, UTF_8))
      Future.value(response)
    }
  }

  class FitnessHandler extends Service[Request, Response] {
    def apply(request: Request) = {
      val input = JsonParser(request.getContentString)
      val (strategy, lineup) = input.convertTo[(Strategy, Lineup)]
      val output = Map("fitness" → strategy.fitness(lineup)).toJson
      val response = Response()
      response.setContent(copiedBuffer(output.compactPrint, UTF_8))
      Future.value(response)
    }
  }
}
