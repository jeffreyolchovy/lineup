package com.olchovy.lineup.domain

import org.scalatest.FunSuite
import org.scalatest.BeforeAndAfter


class LineupSuite extends FunSuite with BeforeAndAfter
{
  import Statistic._

  val lineup = Lineup(Seq(
    Player("Foo", Map(AB → 10, H → 3, `2B` → 1, BB → 1, SF → 2)),
    Player("Bar", Map(AB → 12, H → 5, `3B` → 1, HR → 1, BB → 2, SO → 1))
  ))

  test("Lineup has correct number of players") {
    assert(lineup.size == 2)
  }

  test("Lineup maintains order of players") {
    assert(lineup.players(0).name == "Foo")
    assert(lineup.players(1).name == "Bar")
  }

  test("Lineup has correct aggregate statistics") {
    assert(lineup.stat(AVG) == ((3.0 / 10.0) + (5.0 / 12.0)) / 2)
  }
}

