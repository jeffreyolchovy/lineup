package com.olchovy.lineup
package serde

import scala.util.Try
import org.json4s._
import org.json4s.JsonAST._
import org.json4s.JsonDSL._
import org.slf4j.LoggerFactory
import com.olchovy.ga._

class GeneticAlgorithmJsonSerialization(implicit ev: IndividualLike[Lineup, Player, Double]) {

  import GeneticAlgorithmJsonSerialization._

  val serializer = new CustomSerializer[GeneticAlgorithm[Lineup]](
    implicit format =>
      Function.unlift(deserialize _) ->
      {
        case _: GeneticAlgorithm[_] => throw new UnsupportedOperationException
      }
  )

  def deserialize(json: JValue)(implicit format: Formats): Option[GeneticAlgorithm[Lineup]] = {
    try {
      val chromosomes = (json \ "chromosomes")
        .extract[Array[Player]]
      val initialPopulationSize = (json \ "initial_population_size")
        .extractOpt[Int]
        .getOrElse(BaseballGeneticAlgorithm.DefaultInitialPopulationSize)
      val maxGenerations = (json \ "max_generations")
        .extractOpt[Int]
        .getOrElse(BaseballGeneticAlgorithm.DefaultMaxGenerations)
      val weightedSelectors = (json \ "selectors")
        .extract[Seq[SelectorDto]]
        .map { dto => dto.weight -> dto.toSelector }
      val weightedOperators = (json \ "operators")
        .extract[Seq[OperatorDto]]
        .map { dto => dto.weight -> dto.toOperator }
      Some(
        BaseballGeneticAlgorithm(
          chromosomes,
          initialPopulationSize,
          maxGenerations,
          weightedSelectors,
          weightedOperators
        )
      )
    } catch {
      case e: Exception =>
        log.error("An error was encountered when attempting to deserialize GeneticAlgorithm instance", e)
        None
    }
  }
}

object GeneticAlgorithmJsonSerialization {

  private val log = LoggerFactory.getLogger(getClass)

  val DefaultElitistPercentage = 0.1

  val DefaultTournamentSize = 16

  val DefaultTighteningRatio = 0.25

  case class SelectorDto(weight: Int, `type`: String, features: Map[String, Double]) {
    def toSelector[A, e](implicit ev: IndividualLike[A, _, e]): Selector[A] = {
      `type` match {
        case "top_n" =>
          Selector.elitist[A](
            features.getOrElse("n", DefaultElitistPercentage)
          )
        case "tournament" =>
          Selector.tournament[A](
            features.get("size").map(_.toInt).getOrElse(DefaultTournamentSize),
            features.getOrElse("tightening_ratio", DefaultTighteningRatio)
          )
        case "proportional" =>
          Selector.fitnessProportional[A, e](
            features.getOrElse("tightening_ratio", DefaultTighteningRatio)
          )
      }
    }
  }

  case class OperatorDto(weight: Int, `type`: String) {
    def toOperator[A, e](implicit ev: IndividualLike[A, e, _]): Operator[A] = {
      `type` match {
        case "crossover" => Operator.crossover[A, e]
        case "mutation" => Operator.swapMutation[A, e]
      }
    }
  }
}
