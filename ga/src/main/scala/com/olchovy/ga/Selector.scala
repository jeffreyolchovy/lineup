package com.olchovy.ga

import scala.collection.mutable.Buffer
import scala.util.Random

/** A function that will retain only a select portion of the given population */
trait Selector[A] extends (Set[A] => Set[A])

object Selector {

  def tournament[A](k: Int, tighteningRatio: Double)(implicit ev: IndividualLike[A, _, _]): Selector[A] = new Selector[A] {
    def apply(population: Set[A]): Set[A] = {
      if (population.size < k) {
        val selector = elitist(0.1)
        selector(population)
      } else {
        val n = math.ceil(population.size * tighteningRatio).toInt
        (0 to n).par.flatMap { _ =>
          val buffer = Set.newBuilder[A]
          val players = Seq.fill(k) {
            val next = RandomUtils.randomFrom(population, buffer.result)
            buffer += next
            next
          }
          val scored = scorePopulation(players)
          val topScore = scored.head._1
          scored.collect {
            case (score, a) if score == topScore => a
          }
        }.toSet.seq
      }
    }
  }

  /** Drop the lowest scoring percentage of a population */
  // fixme does this result in inf loop if too low?
  def truncation[A](percentage: Double)(implicit ev: IndividualLike[A, _, _]): Selector[A] = new Selector[A] {
    def apply(population: Set[A]): Set[A] = {
      val selector = elitist(1 - percentage)
      selector(population)
    }
  }

  /** Retain the highest scoring percentage of a population */
  def elitist[A](percentage: Double)(implicit ev: IndividualLike[A, _, _]): Selector[A] = new Selector[A] {
    def apply(population: Set[A]): Set[A] = {
      val topN = math.ceil(population.size * percentage).toInt
      scorePopulation(population)
        .reverse
        .map(_._2)
        .take(topN)
        .toSet
    }
  }

  def fitnessProportional[A, B](tighteningRatio: Double)(implicit ev: IndividualLike[A, _, B]): Selector[A] = new Selector[A] {
    def apply(population: Set[A]): Set[A] = {
      if (population.size > 1) {
        val scored = scorePopulation(population)
        val iterator = ProportionalCollection(scored.head, scored.tail: _*)(ev.fitnessNumeric).iterator
        val n = math.ceil(population.size * tighteningRatio).toInt
        (0 to n).par.map { _ =>
          iterator.next()
        }.toSet.seq
      } else {
        population
      }
    }
  }

  private def scorePopulation[A, B](population: Traversable[A])(implicit ev: IndividualLike[A, _, B]): Seq[(B, A)] = {
    population
      .par
      .map { a => ev.fitness.apply(a) -> a }
      .toSeq
      .seq
      .sortBy(_._1)(ev.fitnessOrdering)
  }
}
