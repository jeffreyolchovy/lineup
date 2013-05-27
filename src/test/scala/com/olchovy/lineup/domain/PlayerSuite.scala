package com.olchovy.lineup.domain

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter

class PlayerSuite extends FunSuite with BeforeAndAfter
{
  import Statistic._

  val name = "Foo"
  val stats: Map[Statistic.Value, Double] = Map(AB → 10, H → 3, `2B` → 1, BB → 1, SF → 2, AVG → 0.500)
  val player = Player(name, stats)

  test("Player has correct name") {
    assert(player.name == "Foo")
  }

  test("Player has non-empty stats") {
    assert(player.stats != Map.empty[Statistic.Value, Double])
  }

  test("Player has default stats for values not supplied to apply method") {
    assert(player.stat(HR) == 0)
  }

  test("Player has correct computed stats") {
    assert(player.stat(AVG) == (player.stat(H) / player.stat(AB)))
  }

  test("Player overrides incorrectly supplied computed stat values with correct values") {
    assert(player.stat(AVG) != 0.500)
  }
}
