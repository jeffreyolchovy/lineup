package com.olchovy.lineup
package serde

import org.json4s._
import org.slf4j.LoggerFactory
import com.olchovy.ga.IndividualLike

object IndividualLikeJsonSerialization {

  private val log = LoggerFactory.getLogger(getClass)

  val serializer = new CustomSerializer[IndividualLike[Lineup, Player, Double]](
    implicit format =>
      Function.unlift(deserialize _) ->
      {
        case _: IndividualLike[_, _, _] => throw new UnsupportedOperationException
      }
  )

  def deserialize(json: JValue)(implicit format: Formats): Option[IndividualLike[Lineup, Player, Double]] = {
    try {
      val fitnessFunctions = (json \ "fitness_functions") match {
        case jsonArray: JArray => jsonArray.arr.map(_.extract[PlayerFitnessFunction])
        case _ => Seq.empty
      }
      Some(
        new IndividualLike.DoubleFitness[Lineup, Player] {
          val fitness = (lineup: Lineup) => {
            lineup.players.zip(fitnessFunctions).map {
              case (player, f) => f(lineup, player)
            }.sum
          }

          def getChromosomes(lineup: Lineup): Array[Player] = {
            lineup.players.toArray.clone
          }

          def fromChromosomes(chromosomes: Array[Player]): Lineup = {
            Lineup(chromosomes.clone.toSeq)
          }
        }
      )
    } catch {
      case e: Exception =>
        log.error("An error was encountered when attempting to deserialize IndividualLike instance", e)
        None
    }
  }
}
