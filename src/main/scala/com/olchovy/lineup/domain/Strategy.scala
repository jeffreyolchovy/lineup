package com.olchovy.lineup.domain

import annotation.tailrec
import collection.mutable
import math.{ceil, min}
import util.Random

/**
 * The base trait of the genetic algorithm
 *
 * A Strategy defines the behaviors of the algorithm's
 * initialization, selection, and reproduction (mutation and crossover) procedures
 * alongside the definition of the main fitness function.
 */
trait Strategy
{
  val lineupSize: Int

  val initPopulationSize: Int

  val initFitnessThreshold: Double

  val maxFitnessThreshold: Double

  val maxGenerations: Int

  def fitness(lineup: Lineup): Double

  def execute(players: Seq[Player]): Set[Lineup] = execute(initialize(players))

  @tailrec
  private def execute(lineups: Set[Lineup], generation: Int = 0): Set[Lineup] = {
    if(generation < maxGenerations) {
      // calculate the fitness threshold that must be reached to retain the lineup
      val generationThreshold = threshold(generation)

      // score lineups, retaining only those that exceed the threshold
      val scoredLineups = lineups.flatMap { lineup ⇒
        fitness(lineup) match {
          case fitness if fitness >= generationThreshold ⇒ Some((fitness, lineup))
          case _ ⇒ None
        }
      }

      // divide lineups into two sets, one to be mutated, one to be recombinated
      val (lineupsToMutate, lineupsToCrossover) = scoredLineups.splitAt(ceil(scoredLineups.size / 2.0).toInt)

      // the set of lineups that have either been mutated or the original (if the mutated one scored lower)
      val mutatedLineups = lineupsToMutate.map {
        case (score, lineup) ⇒
          val mutated = mutate(lineup)
          val mutatedScore = fitness(mutated)
          if(mutatedScore > score) mutated else lineup
      }

      // the set of lineups that have been bred or the originals (if the children have scored lower)
      val recombinatedLineups = lineupsToCrossover.toSeq.grouped(2).collect {
        case seq @ Seq((score1, lineup1), (score2, lineup2)) ⇒
          val (lineup3, lineup4) = crossover(lineup1, lineup2)
          val score3 = fitness(lineup3)
          val score4 = fitness(lineup4)
          (seq ++ Seq((score3, lineup3), (score4, lineup4))).sortWith { (a, b) ⇒ a._1 > b._1 }.take(2).map(_._2)
      }.flatten.toSet

      execute(mutatedLineups ++ recombinatedLineups, generation + 1)
    } else {
      lineups
    }
  }

  private def initialize(seed: Seq[Player]): Set[Lineup] = {
    for (i ← 1 to initPopulationSize) yield Lineup(Random.shuffle(seed).take(lineupSize))
  }.toSet

  private def threshold(generation: Int) = generation match {
    case 0 ⇒ initFitnessThreshold
    case i ⇒ min(generation * 0.1, maxFitnessThreshold)
  }

  private def crossover(lineup1: Lineup, lineup2: Lineup): (Lineup, Lineup) = {
    val i = randomIndex(lineup1)

    val array1 = Array(lineup1.players: _*)
    val array2 = Array(lineup2.players: _*)

    val value1 = array1(i)
    val value2 = array2(i)

    if (value1 == value2)
      crossover(lineup1, lineup2)
    else {
      val index1 = array1.indexOf(value2)
      val index2 = array2.indexOf(value1)

      // lineups can not be recombinated reliably, mutate each independently
      if (index1 == -1 || index2 == -1)
        (mutate(lineup1), mutate(lineup2))
      else {
        array2(i) = value1
        array1(i) = value2

        array1(index1) = value1
        array2(index2) = value2

        (Lineup(array1.toSeq), Lineup(array2.toSeq))
      }
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
    case index if excluding.contains(index) ⇒ randomIndex(lineup, excluding)
    case index ⇒ index
  }
}

trait MemoizingStrategy
{
  this: Strategy ⇒

  private val cache = mutable.Map.empty[Lineup, Double]

  def fitness(lineup: Lineup): Double = lookupFitness(lineup).getOrElse {
    val score = computeFitness(lineup)
    cache(lineup) = score
    score
  }

  protected def lookupFitness(lineup: Lineup): Option[Double] = cache.get(lineup)

  protected def computeFitness(lineup: Lineup): Double
}

abstract class DefaultStrategy extends Strategy with MemoizingStrategy
{
  val initPopulationSize = DefaultStrategy.InitPopulationSize

  val initFitnessThreshold = DefaultStrategy.InitFitnessThreshold

  val maxFitnessThreshold = DefaultStrategy.MaxFitnessThreshold

  val maxGenerations = DefaultStrategy.MaxGenerations
}

object DefaultStrategy
{
  val InitPopulationSize = 2500

  val InitFitnessThreshold = 1.0

  val MaxFitnessThreshold = 4.0

  val MaxGenerations = 100
}

object DefaultBaseballStrategy extends DefaultStrategy
{
  import Statistic._

  val lineupSize = 9

  protected def computeFitness(lineup: Lineup): Double = lineup.players.zipWithIndex.map {
    case (player, index) ⇒ index match {
      case 0 ⇒
        (player.stat(OBA) - lineup.stat(OBA)) +
        (player.stat(`BB/AB`) - lineup.stat(`BB/AB`)) +
        (lineup.stat(PIP) - player.stat(PIP)) +
        (lineup.stat(`HR/H`) - player.stat(`HR/H`))
      case 1 ⇒ 
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(OBA) - lineup.stat(OBA)) + 
        (player.stat(`BB/AB`) - lineup.stat(`BB/AB`)) +
        (lineup.stat(PIP) - player.stat(PIP)) +
        (lineup.stat(EBA) - player.stat(EBA))
      case 2 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(PIP) - lineup.stat(PIP)) +
        (player.stat(`BB/AB`) - lineup.stat(`BB/AB`))
      case 3 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(OBA) - lineup.stat(OBA)) + 
        (player.stat(`HR/H`) - lineup.stat(`HR/H`))
      case 4 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(PIP) - lineup.stat(PIP)) +
        (lineup.stat(`HR/H`) - player.stat(`HR/H`))
      case 5 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(PIP) - lineup.stat(PIP)) +
        (player.stat(OBA) - lineup.stat(OBA)) +
        (player.stat(`SO/AB`) - lineup.stat(`SO/AB`))
      case 6 ⇒
        (player.stat(PIP) - lineup.stat(PIP)) +
        (lineup.stat(OBA) - player.stat(OBA))
      case 7 ⇒
        (player.stat(PIP) - lineup.stat(PIP)) +
        (player.stat(`HR/H`) - lineup.stat(`HR/H`)) +
        (lineup.stat(SLG) - player.stat(SLG)) +
        (lineup.stat(OBA) - player.stat(OBA)) +
        (lineup.stat(`BB/AB`) - player.stat(`BB/AB`))
      case 8 ⇒
        (player.stat(`1B/H`) - lineup.stat(`1B/H`)) +
        (lineup.stat(PIP) - player.stat(PIP)) +
        (lineup.stat(`SO/AB`) - player.stat(`SO/AB`)) +
        (lineup.stat(SLG) - player.stat(SLG)) +
        (lineup.stat(OBA) - player.stat(OBA)) +
        (lineup.stat(`BB/AB`) - player.stat(`BB/AB`))
      case _ ⇒ 0
    }
  }.sum
}

object DefaultSoftballStrategy extends DefaultStrategy
{
  import Statistic._

  val lineupSize = 12

  protected def computeFitness(lineup: Lineup): Double = lineup.players.zipWithIndex.map {
    case (player, index) ⇒ index match {
      case 0 ⇒
        (player.stat(OBA) - lineup.stat(OBA)) +
        (player.stat(`BB/AB`) - lineup.stat(`BB/AB`)) +
        (lineup.stat(PIP) - player.stat(PIP)) +
        (lineup.stat(`HR/H`) - player.stat(`HR/H`))
      case 1 ⇒ 
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(OBA) - lineup.stat(OBA)) + 
        (player.stat(`BB/AB`) - lineup.stat(`BB/AB`)) +
        (lineup.stat(PIP) - player.stat(PIP)) +
        (lineup.stat(EBA) - player.stat(EBA))
      case 2 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(PIP) - lineup.stat(PIP)) +
        (player.stat(`BB/AB`) - lineup.stat(`BB/AB`))
      case 3 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(OBA) - lineup.stat(OBA)) + 
        (lineup.stat(`HR/H`) - player.stat(`HR/H`))
      case 4 | 6 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(PIP) - lineup.stat(PIP)) +
        (lineup.stat(`HR/H`) - player.stat(`HR/H`))
      case 5 | 7 ⇒
        (player.stat(SLG) - lineup.stat(SLG)) +
        (player.stat(PIP) - lineup.stat(PIP)) +
        (player.stat(OBA) - lineup.stat(OBA)) +
        (player.stat(`SO/AB`) - lineup.stat(`SO/AB`))
      case 8 ⇒
        (player.stat(PIP) - lineup.stat(PIP)) +
        (lineup.stat(OBA) - player.stat(OBA))
      case 9 ⇒
        (player.stat(PIP) - lineup.stat(PIP)) +
        (player.stat(`HR/H`) - lineup.stat(`HR/H`)) +
        (lineup.stat(SLG) - player.stat(SLG)) +
        (lineup.stat(OBA) - player.stat(OBA)) +
        (lineup.stat(`BB/AB`) - player.stat(`BB/AB`))
      case 10 | 11 ⇒ 
        (player.stat(`1B/H`) - lineup.stat(`1B/H`)) +
        (lineup.stat(PIP) - player.stat(PIP)) +
        (lineup.stat(`SO/AB`) - player.stat(`SO/AB`)) +
        (lineup.stat(SLG) - player.stat(SLG)) +
        (lineup.stat(OBA) - player.stat(OBA)) +
        (lineup.stat(`BB/AB`) - player.stat(`BB/AB`))
      case _ ⇒ 0
    }
  }.sum
}
