package com.olchovy.lineup

import cc.spray.json._
import com.olchovy.lineup.domain._


package object io
{
  import DefaultJsonProtocol._

  private[io] trait JsonReaderUtils[A]
  {
    this: JsonReader[A] ⇒ 

    import java.io.File
    import scala.io.Source

    def fromString(string: String): A = read(JsonParser(string))

    def fromFile(filename: String): A = {
      val filepath = if(filename.startsWith("/")) filename else absolutePath(filename)
      fromString(Source.fromFile(filepath).mkString)
    }

    private def absolutePath(filename: String): String = {
      "%s%s%s".format(System.getProperty("user.dir"), File.separator, filename)
    }
  }

  implicit object PlayerJsonFormat extends RootJsonFormat[Player]
  {
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

  implicit object LineupJsonFormat extends RootJsonFormat[Lineup] with JsonReaderUtils[Lineup]
  {
    def read(json: JsValue): Lineup = json match {
      case array: JsArray ⇒ Lineup(array.elements.map(PlayerJsonFormat.read _))
      case _ ⇒ deserializationError("Array expected")
    }

    def write(lineup: Lineup): JsValue = JsArray(lineup.players.map(PlayerJsonFormat.write _): _*)
  }

  implicit object StrategyJsonFormat extends RootJsonFormat[Strategy] with JsonReaderUtils[Strategy]
  {
    import com.olchovy.lineup.dsl._

    def read(json: JsValue): Strategy = json match {
      case obj: JsObject ⇒ new Strategy with MemoizingStrategy {
        lazy val LINEUP_SIZE = fitnessSpecification.size

        val INITIAL_POPULATION_SIZE = {
          val value_? = obj.fields.get("initial_population_size")
          value_?.map(_.convertTo[Int]).getOrElse(DefaultStrategy.INITIAL_POPULATION_SIZE)
        }

        val INITIAL_FITNESS_THRESHOLD = {
          val value_? = obj.fields.get("initial_fitness_threshold")
          value_?.map(_.convertTo[Double]).getOrElse(DefaultStrategy.INITIAL_FITNESS_THRESHOLD)
        }

        val MAX_FITNESS_THRESHOLD = {
          val value_? = obj.fields.get("maximum_fitness_threshold")
          value_?.map(_.convertTo[Double]).getOrElse(DefaultStrategy.MAX_FITNESS_THRESHOLD)
        }

        val MAX_GENERATIONS = {
          val value_? = obj.fields.get("maximum_generations")
          value_?.map(_.convertTo[Int]).getOrElse(DefaultStrategy.MAX_GENERATIONS)
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

