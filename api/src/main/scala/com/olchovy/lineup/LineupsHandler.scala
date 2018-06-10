package com.olchovy.lineup

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response, Status}
import com.twitter.util.Future
import org.json4s._
import org.json4s.native.JsonMethods.{compact, parse, render}
import org.json4s.native.Serialization.{read, write}
import com.olchovy.ga.{GeneticAlgorithm, IndividualLike}
import com.olchovy.lineup.serde._

class LineupsHandler extends Service[Request, Response] {

  implicit val format = (DefaultFormats
    + IndividualLikeJsonSerialization.serializer
    + PlayerJsonSerialization.serializer
    + (new GeneticAlgorithmJsonSerialization).serializer)

  def apply(request: Request) = {
    val jsonString = request.getContentString
    implicit val individualLike = read[IndividualLike[Lineup, Player, Double]](jsonString)
    val algorithm = read[GeneticAlgorithm[Lineup]](jsonString)
    val lineups = algorithm()
    val output = write(Map("lineups" -> lineups))
    val response = Response(request.version, Status.Ok)
    response.setContentString(output)
    Future.value(response)
  }
}
