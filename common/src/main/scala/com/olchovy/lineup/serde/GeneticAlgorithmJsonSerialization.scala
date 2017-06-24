package com.olchovy.lineup.serde

import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.json4s.native.Serialization
import com.olchovy.ga._

class GeneticAlgorithmJsonSerialization[A : Manifest] extends Json4sSerialization[GeneticAlgorithm[A]] {

  val serializer = new CustomSerializer[GeneticAlgorithm[A]](
    implicit format => Function.unlift(deserialize _) -> Function.unlift(serialize _)
  )

  def deserialize(json: JValue)(implicit format: Formats): Option[GeneticAlgorithm[A]] = {
    ???
  }

  def serialize(algo: GeneticAlgorithm[A])(implicit format: Formats): JValue = {
    ("initializer" -> serializeInitializer(algo.initializer)) ~
    ("selector" -> serializeSelector(algo.selector)) ~
    ("operator" -> serializeOperator(algo.operator)) ~
    ("terminator" -> serializeTerminator(algo.terminator))
  }

  def serialize(any: Any)(implicit format: Formats): Option[JValue] = {
    any match {
      case algo: GeneticAlgorithm[_] => Some(serialize(algo))
      case _ => None
    }
  }

  def serializeInitializer(initializer: Initializer[A])(implicit format: Formats): JValue = {
    ???
  }

  def serializeSelector(selector: Selector[A])(implicit format: Formats): JValue = {
    ???
  }

  def serializeOperator(operator: Operator[A])(implicit format: Formats): JValue = {
    ???
  }

  def serializeTerminator(terminator: Terminator[A])(implicit format: Formats): JValue = {
    ???
  }
}
