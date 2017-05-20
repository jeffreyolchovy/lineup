package com.olchovy.lineup

import org.scalatest.{FlatSpec, Matchers}

class PlayerSpec extends FlatSpec with Matchers {

  import Statistic._

  behavior of "Player"

  val player = Player(
    name = "Foo",
    AT_BATS -> 10D,
    HITS -> 3D,
    DOUBLES -> 1D,
    WALKS -> 1D,
    SACRIFICE_FLYOUTS -> 2D,
    BATTING_AVG -> 0.500
  )

  it should "have non-empty stats" in {
    player.stats should not be empty
  }

  it should "have default stats for values not supplied upon creation" in {
    player.stats(HOMERUNS) shouldBe 0
  }

  it should "have the correct computed stats" in {
    player.stats(BATTING_AVG) shouldBe (player.stats(HITS) / player.stats(AT_BATS))
  }

  it should "override incorrectly supplied computed stat values with their correct values" in {
    player.stats(BATTING_AVG) should not be (0.5)
    player.stats(BATTING_AVG) should be (0.3)
  }
}
