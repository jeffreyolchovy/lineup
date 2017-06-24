package com.olchovy.lineup.serde

import org.json4s.{CustomSerializer, Formats}
import org.json4s.JsonAST.{JObject, JValue}

trait Json4sSerialization[A] {

  def serializer: CustomSerializer[A]

  def deserialize(json: JValue)(implicit format: Formats): Option[A]

  def serialize(a: A)(implicit format: Formats): JValue

  def serialize(any: Any)(implicit format: Formats): Option[JValue]
}
