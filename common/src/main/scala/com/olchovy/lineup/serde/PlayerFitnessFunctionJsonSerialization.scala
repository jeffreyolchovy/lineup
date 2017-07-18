package com.olchovy.lineup
package serde

import org.json4s._

object PlayerFitnessFunctionJsonSerialization {

  val serializer = new CustomSerializer[PlayerFitnessFunction](
    implicit format => Function.unlift(deserialize _) -> Function.unlift(serialize _)
  )

  def deserialize(json: JValue)(implicit format: Formats): Option[PlayerFitnessFunction] = {
    val input = json match {
      case array: JArray => Some(array.extract[Seq[String]].mkString(" "))
      case JString(value) => Some(value)
      case _ => None
    }
    input.map(PlayerFitnessFunctionParser.apply)
  }

  private def serialize(any: Any)(implicit format: Formats): Option[JValue] = {
    throw new UnsupportedOperationException
  }
}
