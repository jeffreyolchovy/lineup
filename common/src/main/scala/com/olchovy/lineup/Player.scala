package com.olchovy.lineup

import scala.collection.mutable.{Map => MutableMap}

case class Player private(name: String, stats: Map[Statistic, Double])

object Player {

  import Statistic._

  def apply[A : Numeric](name: String, stats: (Statistic, A)*): Player = {
    val ev = implicitly[Numeric[A]]
    val statsMap = stats.toMap.mapValues(ev.toDouble)
    val statsWithDefaultValues = Statistic.values.map { stat => stat -> statsMap.getOrElse(stat, 0D) }.toMap
    val statsWithComputedValues = computeRemainingStats(statsWithDefaultValues)
    Player(name, statsWithComputedValues)
  }

  private def computeRemainingStats(stats: Map[Statistic, Double]): Map[Statistic, Double] = {
    val writableStats = MutableMap(stats.toSeq: _*)
    writableStats(SINGLES) =
      (writableStats(HITS) - (writableStats(DOUBLES) + writableStats(TRIPLES) + writableStats(HOMERUNS)))
    writableStats(BATTING_AVG) =
      (writableStats(HITS) / writableStats(AT_BATS))
    writableStats(SLUGGING_PCT) =
      ((2 * writableStats(DOUBLES)) +
       (3 * writableStats(TRIPLES)) +
       (4 * writableStats(HOMERUNS))
      ) / writableStats(AT_BATS)
    writableStats(ON_BASE_AVG) =
      (writableStats(HITS) + writableStats(WALKS)) /
      (writableStats(AT_BATS) + writableStats(WALKS) + writableStats(SACRIFICE_FLYOUTS))
    writableStats(ISOLATED_POWER) =
      writableStats(SLUGGING_PCT) - writableStats(BATTING_AVG)
    writableStats(PUT_IN_PLAY_PCT) =
      (1 - (writableStats(STRIKEOUTS) / writableStats(AT_BATS)))
    writableStats(HOMERUNS_PER_HIT) =
      writableStats(HOMERUNS) / writableStats(HITS)
    writableStats(WALKS_PER_AT_BAT) =
      writableStats(WALKS) / writableStats(AT_BATS)
    writableStats(STRIKEOUTS_PER_AT_BAT) =
      writableStats(STRIKEOUTS) / writableStats(AT_BATS)
    writableStats(SINGLES_PER_HIT) =
      writableStats(SINGLES) / writableStats(HITS)
    writableStats.toMap
  }
}
