package com.olchovy.ga

import scala.util.Random

/** A function that transforms/mutates a given population into a new population */
trait Operator[A] extends (Set[A] => Set[A])

object Operator {

  /** Individuals with binary chromosomes will have one chromosome flipped */
  def binaryPointMutation[A](implicit ev: IndividualLike[A, Boolean, _]): Operator[A] = {
    new Operator[A] {
      def apply(population: Set[A]): Set[A] = {
        applyToPopulation(population)(invertAtPoint)
      }
    }
  }

  /** Individuals will have one chromosome replaced */
  def replacementPointMutation[A, B](replacement: => B)(implicit ev: IndividualLike[A, B, _]): Operator[A] = {
    new Operator[A] {
      def apply(population: Set[A]): Set[A] = {
        applyToPopulation(population) { chromosomes: Array[B] =>
          replaceAtPoint(replacement, chromosomes)
        }
      }
    }
  }

  /** Individuals will have one chromosome swapped with another */
  def swapMutation[A, B](implicit ev: IndividualLike[A, B, _]): Operator[A] = {
    new Operator[A] {
      def apply(population: Set[A]): Set[A] = {
        applyToPopulation(population) { chromosomes: Array[B] =>
          mutate(chromosomes)
        }
      }
    }
  }

  /** A pair of individuals will swap their chromosomes at a random index */
  def crossover[A, B](implicit ev: IndividualLike[A, B, _]): Operator[A] = {
    new Operator[A] {
      def apply(population: Set[A]): Set[A] = {
        population.toSeq.grouped(2).flatMap {
          case Seq(a) =>
            val chromosomes = ev.getChromosomes(a)
            Seq(a, ev.fromChromosomes(mutate(chromosomes)))
          case Seq(a1, a2) =>
            val chromosomes1 = ev.getChromosomes(a1)
            val chromosomes2 = ev.getChromosomes(a2)
            val (chromosomes3, chromosomes4) = crossover(chromosomes1, chromosomes2)
            Seq(a1, a2, ev.fromChromosomes(chromosomes3), ev.fromChromosomes(chromosomes4))
        }.toSet
      }
    }
  }

  /** Apply the given operator to a population and union the result of the operator with the initial population */
  private def applyToPopulation[A, B]
    (population: Set[A])
    (f: Array[B] => Array[B])
    (implicit ev: IndividualLike[A, B, _]): Set[A] = {
    population.flatMap { a =>
      val chromosomes = ev.getChromosomes(a)
      Set(a, ev.fromChromosomes(f(chromosomes)))
    }
  }

  @inline
  def invertAtPoint(chromosomes: Array[Boolean]): Array[Boolean] = {
    val i = RandomUtils.randomIndex(chromosomes)
    val tmp = chromosomes(i)
    chromosomes(i) = !tmp
    chromosomes
  }

  @inline
  def replaceAtPoint[A](replacement: A, chromosomes: Array[A]): Array[A] = {
    val i = RandomUtils.randomIndex(chromosomes)
    val tmp = chromosomes(i)
    chromosomes(i) = replacement
    chromosomes
  }

  @inline
  def mutate[A](chromosomes: Array[A]): Array[A] = {
    val i = RandomUtils.randomIndex(chromosomes)
    val j = RandomUtils.randomIndex(chromosomes, i)
    val tmp = chromosomes(i)
    chromosomes(i) = chromosomes(j)
    chromosomes(j) = tmp
    chromosomes
  }

  @inline
  @annotation.tailrec
  def crossover[A](chromosomes1: Array[A], chromosomes2: Array[A]): (Array[A], Array[A]) = {
    val i = RandomUtils.randomIndex(chromosomes1)
    val value1 = chromosomes1(i)
    val value2 = chromosomes2(i)

    if (value1 == value2) {
      crossover(chromosomes1, chromosomes2)
    } else {
      val index1 = chromosomes1.indexOf(value2)
      val index2 = chromosomes2.indexOf(value1)

      // can not be recombinated reliably, mutate each independently
      if (index1 == -1 || index2 == -1) {
        (mutate(chromosomes1), mutate(chromosomes2))
      } else {
        chromosomes2(i) = value1
        chromosomes1(i) = value2

        chromosomes1(index1) = value1
        chromosomes2(index2) = value2

        (chromosomes1, chromosomes2)
      }
    }
  }
}
