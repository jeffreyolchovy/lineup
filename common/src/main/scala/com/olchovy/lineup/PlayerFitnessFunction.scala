package com.olchovy.lineup

import PlayerFitnessFunction._

case class PlayerFitnessFunction(rules: Rule*) extends ((Lineup, Player) => Double) {
  def apply(lineup: Lineup, player: Player): Double = {
    rules.map {
      case Rule(BetterThanAvg, stat) => player.stats(stat) - lineup.stats(stat)
      case Rule(WorseThanAvg, stat) => lineup.stats(stat) - player.stats(stat)
    }.sum
  }
}

object PlayerFitnessFunction {

  case class Rule(spec: Specification, stat: Statistic)

  sealed trait Specification
  case object BetterThanAvg extends Specification
  case object WorseThanAvg extends Specification
}
