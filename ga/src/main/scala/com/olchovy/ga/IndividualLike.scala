package com.olchovy.ga

trait IndividualLike[A, Chromosome, Fitness] {

  implicit def fitnessNumeric: Numeric[Fitness]

  implicit def fitnessOrdering: Ordering[Fitness]

  def fitness: FitnessFunction[A, Fitness]

  def getChromosomes(a: A): Array[Chromosome]

  def fromChromosomes(chromosomes: Array[Chromosome]): A
}

object IndividualLike {

  trait DoubleFitness[A, Chromosome] extends IndividualLike[A, Chromosome, Double] {
    val fitnessNumeric: Numeric[Double] = implicitly[Numeric[Double]]
    val fitnessOrdering: Ordering[Double] = implicitly[Ordering[Double]]
  }

  trait IntFitness[A, Chromosome] extends IndividualLike[A, Chromosome, Int] {
    val fitnessNumeric: Numeric[Int] = implicitly[Numeric[Int]]
    val fitnessOrdering: Ordering[Int] = implicitly[Ordering[Int]]
  }
}
