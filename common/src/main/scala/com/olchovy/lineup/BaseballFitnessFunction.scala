package com.olchovy.lineup

import Statistic._

object BaseballFitnessFunction extends (Lineup => Double) {

  def apply(lineup: Lineup): Double = {
    lineup.players.zipWithIndex.map { case (player, i) =>
      i match {
        case 0 ⇒
          (player.stats(ON_BASE_AVG) - lineup.stats(ON_BASE_AVG)) +
          (player.stats(WALKS_PER_AT_BAT) - lineup.stats(WALKS_PER_AT_BAT)) +
          (lineup.stats(PUT_IN_PLAY_PCT) - player.stats(PUT_IN_PLAY_PCT)) +
          (lineup.stats(HOMERUNS_PER_HIT) - player.stats(HOMERUNS_PER_HIT))
        case 1 ⇒ 
          (player.stats(SLUGGING_PCT) - lineup.stats(SLUGGING_PCT)) +
          (player.stats(ON_BASE_AVG) - lineup.stats(ON_BASE_AVG)) + 
          (player.stats(WALKS_PER_AT_BAT) - lineup.stats(WALKS_PER_AT_BAT)) +
          (lineup.stats(PUT_IN_PLAY_PCT) - player.stats(PUT_IN_PLAY_PCT)) +
          (lineup.stats(EXTRA_BASE_AVG) - player.stats(EXTRA_BASE_AVG))
        case 2 ⇒
          (player.stats(SLUGGING_PCT) - lineup.stats(SLUGGING_PCT)) +
          (player.stats(PUT_IN_PLAY_PCT) - lineup.stats(PUT_IN_PLAY_PCT)) +
          (player.stats(WALKS_PER_AT_BAT) - lineup.stats(WALKS_PER_AT_BAT))
        case 3 ⇒
          (player.stats(SLUGGING_PCT) - lineup.stats(SLUGGING_PCT)) +
          (player.stats(ON_BASE_AVG) - lineup.stats(ON_BASE_AVG)) + 
          (player.stats(HOMERUNS_PER_HIT) - lineup.stats(HOMERUNS_PER_HIT))
        case 4 ⇒
          (player.stats(SLUGGING_PCT) - lineup.stats(SLUGGING_PCT)) +
          (player.stats(PUT_IN_PLAY_PCT) - lineup.stats(PUT_IN_PLAY_PCT)) +
          (lineup.stats(HOMERUNS_PER_HIT) - player.stats(HOMERUNS_PER_HIT))
        case 5 ⇒
          (player.stats(SLUGGING_PCT) - lineup.stats(SLUGGING_PCT)) +
          (player.stats(PUT_IN_PLAY_PCT) - lineup.stats(PUT_IN_PLAY_PCT)) +
          (player.stats(ON_BASE_AVG) - lineup.stats(ON_BASE_AVG)) +
          (player.stats(STRIKEOUTS_PER_AT_BAT) - lineup.stats(STRIKEOUTS_PER_AT_BAT))
        case 6 ⇒
          (player.stats(PUT_IN_PLAY_PCT) - lineup.stats(PUT_IN_PLAY_PCT)) +
          (lineup.stats(ON_BASE_AVG) - player.stats(ON_BASE_AVG))
        case 7 ⇒
          (player.stats(PUT_IN_PLAY_PCT) - lineup.stats(PUT_IN_PLAY_PCT)) +
          (player.stats(HOMERUNS_PER_HIT) - lineup.stats(HOMERUNS_PER_HIT)) +
          (lineup.stats(SLUGGING_PCT) - player.stats(SLUGGING_PCT)) +
          (lineup.stats(ON_BASE_AVG) - player.stats(ON_BASE_AVG)) +
          (lineup.stats(WALKS_PER_AT_BAT) - player.stats(WALKS_PER_AT_BAT))
        case 8 ⇒
          (player.stats(SINGLES_PER_HIT) - lineup.stats(SINGLES_PER_HIT)) +
          (lineup.stats(PUT_IN_PLAY_PCT) - player.stats(PUT_IN_PLAY_PCT)) +
          (lineup.stats(STRIKEOUTS_PER_AT_BAT) - player.stats(STRIKEOUTS_PER_AT_BAT)) +
          (lineup.stats(SLUGGING_PCT) - player.stats(SLUGGING_PCT)) +
          (lineup.stats(ON_BASE_AVG) - player.stats(ON_BASE_AVG)) +
          (lineup.stats(WALKS_PER_AT_BAT) - player.stats(WALKS_PER_AT_BAT))
        case _ ⇒ 0
      }
    }
  }.sum
}
