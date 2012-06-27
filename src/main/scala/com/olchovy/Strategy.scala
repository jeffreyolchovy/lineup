package com.olchovy

import math.{ceil, min}
import util.Random
import com.olchovy.domain._


trait Strategy
{
  val INITIAL_POPULATION_SIZE: Int

  val INITIAL_FITNESS_THRESHOLD: Double

  val MAX_FITNESS_THRESHOLD: Double

  val MAX_GENERATIONS: Int

  def fitness(lineup: Lineup): Double

  def execute(lineup: Lineup): Set[Lineup] = execute(initialize(lineup))

  private def initialize(seed: Lineup): Set[Lineup] = {
    Set(seed) ++ (for(i ← 1 to INITIAL_POPULATION_SIZE) yield Lineup(Random.shuffle(seed.players)))
  }

  private def execute(lineups: Set[Lineup], generation: Int = 0): Set[Lineup] = {
    if(generation < MAX_GENERATIONS) {
      val scoredLineups = lineups.flatMap { lineup =>
        fitness(lineup) match {
          case fitness if fitness > threshold(generation) => Some((fitness, lineup))
          case _ => None
        }
      }

      val (lineupsToMutate, lineupsToCrossover) = scoredLineups.splitAt(ceil(scoredLineups.size / 2.0).toInt)

      val mutatedLineups = lineupsToMutate.map {
        case (score, lineup) =>
          val mutated = mutate(lineup)
          val mutatedScore = fitness(mutated)
          if(mutatedScore > score) mutated else lineup
      }

      val recombinatedLineups = lineupsToCrossover.toSeq.grouped(2).collect {
        case Seq((score1, lineup1), (score2, lineup2)) =>
          val (lineup3, lineup4) = crossover(lineup1, lineup2)
          val score3 = fitness(lineup3)
          val score4 = fitness(lineup4)
          Seq(if(score3 > score1) lineup3 else lineup1, if(score4 > score2) lineup4 else lineup2)
      }.flatten.toSet

      execute(mutatedLineups ++ recombinatedLineups, generation + 1)
    } else {
      lineups
    }
  }

  private def threshold(generation: Int) = generation match {
    case 0 => INITIAL_FITNESS_THRESHOLD
    case i => min(generation * 0.1, MAX_FITNESS_THRESHOLD)
  }

  private def crossover(lineup1: Lineup, lineup2: Lineup): (Lineup, Lineup) = {
    val i = randomIndex(lineup1)

    val array1 = Array(lineup1.players: _*)
    val array2 = Array(lineup2.players: _*)

    val value1 = array1(i)
    val value2 = array2(i)

    if(value1 == value2)
      crossover(lineup1, lineup2)
    else {
      val index1 = array1.indexOf(value2)
      val index2 = array2.indexOf(value1)

      array2(i) = value1
      array1(i) = value2

      array1(index1) = value1
      array2(index2) = value2

      (Lineup(array1.toSeq), Lineup(array2.toSeq))
    }
  }

  private def mutate(lineup: Lineup): Lineup = {
    val array = Array(lineup.players: _*)
    val i = randomIndex(lineup)
    val j = randomIndex(lineup, Set(i))

    val tmp = array(i)
    array(i) = array(j)
    array(j) = tmp

    Lineup(array.toSeq)
  }

  private def randomIndex(lineup: Lineup): Int = Random.nextInt(lineup.size) 

  private def randomIndex(lineup: Lineup, excluding: Set[Int]): Int = randomIndex(lineup) match {
    case index if excluding.contains(index) => randomIndex(lineup, excluding)
    case index => index
  }
}

class DefaultStrategy extends Strategy
{
  val INITIAL_POPULATION_SIZE = 2500

  val INITIAL_FITNESS_THRESHOLD = 1.0

  val MAX_FITNESS_THRESHOLD = 4.0

  val MAX_GENERATIONS = 100

  def fitness(lineup: Lineup): Double = lineup.players.zipWithIndex.map {
    case (player, index) => index match {
      case 0 =>
        (player.statistics.OBA - lineup.OBA) +
        (player.statistics.`BB/AB` - lineup.`BB/AB`) +
        (lineup.PIP - player.statistics.PIP) +
        (lineup.`HR/H` - player.statistics.`HR/H`)
      case 1 => 
        (player.statistics.SLG - lineup.SLG) +
        (player.statistics.OBA - lineup.OBA) + 
        (player.statistics.`BB/AB` - lineup.`BB/AB`) +
        (lineup.PIP - player.statistics.PIP) +
        (lineup.EBA - player.statistics.EBA)
      case 2 =>
        (player.statistics.SLG - lineup.SLG) +
        (player.statistics.PIP - lineup.PIP) +
        (player.statistics.`BB/AB` - lineup.`BB/AB`)
      case 3 =>
        (player.statistics.SLG - lineup.SLG) +
        (player.statistics.OBA - lineup.OBA) + 
        (player.statistics.`HR/H` - lineup.`HR/H`)
      case 4 | 6 =>
        (player.statistics.SLG - lineup.SLG) +
        (player.statistics.PIP - lineup.PIP) +
        (lineup.`HR/H` - player.statistics.`HR/H`)
      case 5 | 7 =>
        (player.statistics.SLG - lineup.SLG) +
        (player.statistics.PIP - lineup.PIP) +
        (player.statistics.OBA - lineup.OBA) +
        (player.statistics.`SO/AB` - lineup.`SO/AB`)
      case 8 =>
        (player.statistics.PIP - lineup.PIP) +
        (lineup.OBA - player.statistics.OBA)
      case 9 =>
        (player.statistics.PIP - lineup.PIP) +
        (player.statistics.`HR/H` - lineup.`HR/H`) +
        (lineup.SLG - player.statistics.SLG) +
        (lineup.OBA - player.statistics.OBA) +
        (lineup.`BB/AB` - player.statistics.`BB/AB`)
      case 10 =>
        (player.statistics.`1B/H` - lineup.`1B/H`) +
        (lineup.PIP - player.statistics.PIP) +
        (lineup.`SO/AB` - player.statistics.`SO/AB`) +
        (lineup.SLG - player.statistics.SLG) +
        (lineup.OBA - player.statistics.OBA) +
        (lineup.`BB/AB` - player.statistics.`BB/AB`)
      //case 11 =>
      //case 12 =>
      case _ => 0
    }
  }.sum
}

