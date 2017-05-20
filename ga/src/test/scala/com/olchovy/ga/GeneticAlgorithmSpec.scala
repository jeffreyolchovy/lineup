package com.olchovy.ga

import scala.util.Random
import org.scalatest.{FlatSpec, Matchers}

class GeneticAlgorithmSpec extends FlatSpec with Matchers {

  behavior of "GeneticAlgorithm.Builder"

  it should "build a GeneticAlgorithm given individual components [boolean]" in {
    implicit val individualLike = IndividualLikeImpls.individualLikeBitSeq
    val sample = Seq(true, true, false, false)
    val ideal = Seq(true, true, true, true)
    val algorithm = GeneticAlgorithm.newBuilder[Seq[Boolean]]
      .withInitializer(Initializer.fromSample(sample, populationSize = 4)(Random.shuffle))
      .withSelector(Selector.elitist(percentage = 0.1))
      .withOperator(Operator.binaryPointMutation)
      .withTerminator(Terminator.criteriaBased(_.currentPopulation.contains(ideal)))
      .build()
    val result = algorithm()
    result should contain (ideal)
  }

  it should "build a GeneticAlgorithm given individual components [int]" in {
    implicit val individualLike = IndividualLikeImpls.individualLikeIntSeq
    def intStream(n: Int): Stream[Int] = n #:: intStream(n + 1)
    val size = 10
    val sample = Seq.fill(size)(Random.nextInt(size))
    val ideal = intStream(0).take(size).toSeq
    val selector = CompositeProportionalSelector(
      1 -> Selector.tournament(k = 32, tighteningRatio = 0.25),
      2 -> Selector.elitist(percentage = 0.1)
    )
    val operator = CompositeProportionalOperator(
      1 -> Operator.replacementPointMutation(Random.nextInt(size)),
      3 -> Operator.crossover
    )
    val algorithm = GeneticAlgorithm.newBuilder[Seq[Int]]
      .withInitializer(Initializer.fromSample(sample, populationSize = 2500)(Random.shuffle))
      .withSelector(selector)
      .withOperator(operator)
      .withTerminator(Terminator.criteriaBased(_.currentPopulation.contains(ideal)))
      .build()
    val result = algorithm()
    result should contain (ideal)
  }
}
