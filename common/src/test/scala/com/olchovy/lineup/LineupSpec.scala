package com.olchovy.lineup

import org.scalatest.{FlatSpec, Matchers}
import com.olchovy.ga._

class LineupSpec extends FlatSpec with Matchers {

  import Statistic._

  behavior of "Lineup"

  val lineup = Lineup(
    Player("Foo", AT_BATS -> 10, HITS -> 3, DOUBLES -> 1, WALKS -> 1, SACRIFICE_FLYOUTS -> 2),
    Player("Bar", AT_BATS -> 12, HITS -> 5, TRIPLES -> 1, HOMERUNS -> 1, WALKS -> 2, STRIKEOUTS -> 1)
  )

  it should "have correct number of players" in {
    lineup should have size 2
  }

  it should "maintain order of players" in {
    val players = lineup.players
    players(0).name shouldBe "Foo"
    players(1).name shouldBe "Bar"
  }

  it should "have the correct aggregate statistics" in {
    lineup.stats(BATTING_AVG) shouldBe (((3.0 / 10.0) + (5.0 / 12.0)) / 2)
  }

  behavior of "Lineup.IndividualLikeLineup"

  it should "be able to be leveraged to build GeneticAlgorithm instances" in {

    import Lineup._

    val players = Array(
      Player("A", AT_BATS -> 10, HITS -> 3, DOUBLES -> 1, WALKS -> 1, SACRIFICE_FLYOUTS -> 1),
      Player("B", AT_BATS -> 12, HITS -> 5, DOUBLES -> 2, WALKS -> 2, STRIKEOUTS -> 1),
      Player("C", AT_BATS -> 8, HITS -> 5, TRIPLES -> 1, HOMERUNS -> 1, WALKS -> 5, STRIKEOUTS -> 2),
      Player("D", AT_BATS -> 19, HITS -> 11, DOUBLES -> 4, HOMERUNS -> 1, WALKS -> 4, STRIKEOUTS -> 1),
      Player("E", AT_BATS -> 10, HITS -> 5, TRIPLES -> 1, WALKS -> 2, STRIKEOUTS -> 1),
      Player("F", AT_BATS -> 7, HITS -> 2, DOUBLES -> 1, HOMERUNS -> 1, WALKS -> 3, SACRIFICE_FLYOUTS -> 1),
      Player("G", AT_BATS -> 9, HITS -> 3, DOUBLES -> 2, HOMERUNS -> 1, WALKS -> 1, STRIKEOUTS -> 1),
      Player("H", AT_BATS -> 11, HITS -> 3, DOUBLES -> 1, HOMERUNS -> 2, WALKS -> 1, STRIKEOUTS -> 6),
      Player("I", AT_BATS -> 12, HITS -> 8, DOUBLES -> 2,  WALKS -> 4, SACRIFICE_FLYOUTS -> 2),
      Player("J", AT_BATS -> 8, HITS -> 5, DOUBLES -> 3, HOMERUNS -> 1, WALKS -> 1, STRIKEOUTS -> 1)
    )

    val results = GeneticAlgorithm.newBuilder[Lineup]
      .withInitializer(Initializer.fromChromosomes(players, populationSize = 30) {
        RandomUtils.arrayShuffle(_).take(9)
      })
      .withSelector(Selector.elitist(percentage = 0.1))
      .withOperator(Operator.crossover)
      .withTerminator(Terminator.generationBased(maxGenerations = 2500))
      .build()
      .apply()

    val scores = results.map(IndividualLikeLineup.fitness)

    val arbitraryLineup = Lineup(players.take(9).toSeq)
    val arbitraryScore = IndividualLikeLineup.fitness(arbitraryLineup)

    all (scores) should be > arbitraryScore

    Lineup.visualize(arbitraryLineup)
    results.foreach(Lineup.visualize)
  }
}
