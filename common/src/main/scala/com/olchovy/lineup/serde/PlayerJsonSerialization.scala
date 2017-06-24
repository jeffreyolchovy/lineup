package com.olchovy.lineup
package serde

import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization

object PlayerJsonSerialization extends Json4sSerialization[Player] {

  val serializer = new CustomSerializer[Player](
    implicit format => Function.unlift(deserialize _) -> Function.unlift(serialize _)
  )

  def deserialize(json: JValue)(implicit format: Formats): Option[Player] = {
    val statAbbrs = Statistic.values.map(_.getAbbreviation).toSet
    for {
      name <- (json \ "name").extractOpt[String]
      statsByAbbr <- (json \ "statistics").extractOpt[Map[String, Double]]
      abbrs = statsByAbbr.keySet if abbrs.subsetOf(statAbbrs)
      stats = statsByAbbr.map { case (abbr, value) => (Statistic.fromAbbreviation(abbr), value) }
    } yield {
      Player(name, stats.toSeq: _*)
    }
  }

  def serialize(player: Player)(implicit format: Formats): JValue = {
    val statsByAbbr = player.stats.map { case (stat, value) => (stat.getAbbreviation, value) }
    ("name" -> player.name) ~ ("statistics" -> statsByAbbr)
  }

  def serialize(any: Any)(implicit format: Formats): Option[JValue] = {
    any match {
      case player: Player => Some(serialize(player))
      case _ => None
    }
  }
}
