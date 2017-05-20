package com.olchovy.ga

/** Initialize a population of type A */
trait Initializer[A] {
  def initialize(): Set[A]
}

object Initializer {

  /** Use the given population of type A as the initial population */
  def fromStaticPopulation[A](population: Set[A]): Initializer[A] = new Initializer[A] {
    def initialize(): Set[A] = {
      population
    }
  }

  /** Initialize a population of type A given a factory function to create instances of type A */
  def fromFactoryFunction[A](populationSize: Int)(f: => A): Initializer[A] = new Initializer[A] {
    def initialize(): Set[A] = {
      Stream.continually(f).distinct.take(populationSize).toSet
    }
  }

  /** Initialize a population of type A given an instance of type B */
  def fromSeed[A, B](seed: B, populationSize: Int)(f: B => A): Initializer[A] = {
    fromFactoryFunction(populationSize)(f(seed))
  }

  /** Initialize a population of type A given an sample instance of type A */
  def fromSample[A](sample: A, populationSize: Int)(f: A => A): Initializer[A] = {
    fromSeed(sample, populationSize)(f)
  }

  /** Initialize a population of type A given a sample set of chromosomes for type A */
  def fromChromosomes[A, B](chromosomes: Array[B], populationSize: Int)(f: Array[B] => Array[B])(implicit ev: IndividualLike[A, B, _]): Initializer[A] = {
    fromSeed(chromosomes, populationSize)(f andThen ev.fromChromosomes)
  }
}
