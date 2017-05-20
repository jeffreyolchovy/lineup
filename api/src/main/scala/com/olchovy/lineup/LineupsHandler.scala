package com.olchovy.lineup

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
//import spray.json._
//import com.olchovy.lineup.serde.ApiJsonProtocol._

class LineupsHandler extends Service[Request, Response] {
  def apply(request: Request) = {
    /*
    val input = ""//JsonParser(request.getContentString)
    val (strategy, lineup) = (null, null)//input.convertTo[(Strategy, Lineup)]
    val lineups = strategy.execute(lineup.players).map { lineup =>
      (strategy.fitness(lineup), lineup.players.map(_.name))
    }.toSeq.sortWith { (a, b) =>
      a._1 > b._1
    }.map(_._2)
    */
    val output = ""//Map("lineups" -> lineups).toJson
    val response = Response(request.version, Status.Ok)
    response.setContentString(output/*.compactPrint*/)
    Future.value(response)
  }
}
