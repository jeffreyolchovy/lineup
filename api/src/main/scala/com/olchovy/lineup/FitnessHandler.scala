package com.olchovy.lineup

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
//import spray.json._
//import com.olchovy.lineup.serde.ApiJsonProtocol._

class FitnessHandler extends Service[Request, Response] {
  def apply(request: Request) = {
    /*
    val input = ""//JsonParser(request.getContentString)
    val (strategy, lineup) = (null, null)//input.convertTo[(Strategy, Lineup)]
    */
    val output = ""//Map("fitness" -> strategy.fitness(lineup)).toJson
    val response = Response(request.version, Status.Ok)
    response.setContentString(output/*.compactPrint*/)
    Future.value(response)
  }
}
