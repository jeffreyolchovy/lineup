package com.olchovy.lineup

import com.olchovy.ga._

object BaseballGeneticAlgorithm {

  val DefaultInitialPopulationSize: Int = 2500

  val DefaultSelectorPercentage: Double = 0.10

  val DefaultMaxGenerations: Int = 500

  def defaultSelector(implicit ev: IndividualLike[Lineup, Player, Double]): Selector[Lineup] = {
    Selector.elitist(DefaultSelectorPercentage)
  }

  def defaultOperator(implicit ev: IndividualLike[Lineup, Player, Double]): Operator[Lineup] = {
    Operator.crossover
  }

  def apply(
    chromosomes: Array[Player],
    initialPopulationSize: Int = DefaultInitialPopulationSize,
    maxGenerations: Int = DefaultMaxGenerations,
    weightedSelectors: Seq[(Int, Selector[Lineup])] = Seq(1 -> defaultSelector),
    weightedOperators: Seq[(Int, Operator[Lineup])] = Seq(1 -> defaultOperator)
  )(implicit ev: IndividualLike[Lineup, Player, Double]): GeneticAlgorithm[Lineup] = {
    require(chromosomes.nonEmpty)
    require(weightedSelectors.nonEmpty)
    require(weightedOperators.nonEmpty)

    GeneticAlgorithm.newBuilder[Lineup]
      .withInitializer(
        Initializer.fromChromosomes(chromosomes, DefaultInitialPopulationSize)(RandomUtils.arrayShuffle)
      )
      .withSelector(
        CompositeProportionalSelector(weightedSelectors.head, weightedSelectors.tail: _*)
      )
      .withOperator(
        CompositeProportionalOperator(weightedOperators.head, weightedOperators.tail: _*)
      )
      .withTerminator(
        Terminator.generationBased(DefaultMaxGenerations)
      )
      .build()
  }
}
