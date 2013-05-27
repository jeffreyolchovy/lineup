package com.olchovy.lineup

import cc.spray.json._
import cc.spray.json.DefaultJsonProtocol._
import com.olchovy.lineup.domain._
import com.olchovy.lineup.dsl._

package object io {

  implicit object PlayerJsonFormat extends RootJsonFormat[Player] {
    def read(json: JsValue): Player = json match {
      case obj: JsObject ⇒
        val name = obj.fields("name").convertTo[String]
        val stats = {
          val keys = Statistic.values.map(_.toString)
          obj.fields("stats").asJsObject.fields.flatMap { 
            case (key, value) if keys.contains(key) ⇒ Some(Statistic.withName(key) → value.convertTo[Double])
            case _ ⇒ None
          }
        }

        Player(name, stats)

      case _ ⇒ deserializationError("Object literal expected")
    }

    def write(player: Player): JsValue = JsObject(
      "name" → new JsString(player.name),
      "stats" → new JsObject(player.stats.map {
        case (key, value) ⇒ key.toString → DoubleJsonFormat.write(value)
      })
    )
  }

  implicit object LineupJsonFormat extends RootJsonFormat[Lineup] {
    def read(json: JsValue): Lineup = json match {
      case array: JsArray ⇒ Lineup(array.elements.map(PlayerJsonFormat.read _))
      case _ ⇒ deserializationError("Array expected")
    }

    def write(lineup: Lineup): JsValue = JsArray(lineup.players.map(PlayerJsonFormat.write _): _*)
  }

  implicit object StrategyJsonFormat extends RootJsonFormat[Strategy] {
    def read(json: JsValue): Strategy = json match {
      case obj: JsObject ⇒ new Strategy with MemoizingStrategy {
        lazy val lineupSize = fitnessSpecification.size

        val initPopulationSize = {
          val value_? = obj.fields.get("initial_population_size")
          value_?.map(_.convertTo[Int]).getOrElse(DefaultStrategy.InitPopulationSize)
        }

        val initFitnessThreshold = {
          val value_? = obj.fields.get("initial_fitness_threshold")
          value_?.map(_.convertTo[Double]).getOrElse(DefaultStrategy.InitFitnessThreshold)
        }

        val maxFitnessThreshold = {
          val value_? = obj.fields.get("maximum_fitness_threshold")
          value_?.map(_.convertTo[Double]).getOrElse(DefaultStrategy.MaxFitnessThreshold)
        }

        val maxGenerations = {
          val value_? = obj.fields.get("maximum_generations")
          value_?.map(_.convertTo[Int]).getOrElse(DefaultStrategy.MaxGenerations)
        }

        protected def computeFitness(lineup: Lineup): Double = lineup.players.zipWithIndex.map {
          case (player, index) if fitnessSpecification.size > index ⇒ fitnessSpecification(index)(lineup, player) 
          case _ ⇒ 0
        }.sum

        private val fitnessSpecification: Seq[(Lineup, Player) ⇒ Double] = obj.fields("fitness_specification") match {
          case array: JsArray ⇒ array.elements.zipWithIndex.map {
            case (value: JsArray, i) ⇒ FitnessSpecificationParser(value.elements.map(_.convertTo[String]).mkString(" "))
            case (value: JsString, i) ⇒ FitnessSpecificationParser(value.convertTo[String])
            case (_, i) ⇒ deserializationError("Array or string expected for 'fitness_specification' element " + i)
          }

          case _ ⇒ deserializationError("Array expected for property 'fitness_specification'")
        }
      }

      case _ ⇒ deserializationError("Object literal expected")
    }

    def write(strategy: Strategy): JsValue = serializationError("Strategy is read-only")
  }
}
