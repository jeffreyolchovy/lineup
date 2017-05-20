package com.olchovy.lineup

import com.olchovy.ga.IndividualLike

case class Lineup(players: Seq[Player]) {
  def size: Int = players.size
  def stats(name: Statistic): Double = players.map(_.stats(name)).sum / size
}

object Lineup {

  def apply(player1: Player, players: Player*): Lineup = {
    Lineup(player1 +: players)
  }

  implicit object IndividualLikeLineup extends IndividualLike.DoubleFitness[Lineup, Player] {

    val fitness = BaseballFitnessFunction(_)

    def getChromosomes(lineup: Lineup): Array[Player] = {
      lineup.players.toArray.clone
    }

    def fromChromosomes(chromosomes: Array[Player]): Lineup = {
      Lineup(chromosomes.clone.toSeq)
    }
  }

  def visualize(lineup: Lineup): Unit = {
    val score = "%1.3f".format(IndividualLikeLineup.fitness(lineup))
    val players = lineup.players.map { player =>
      s"${player.name}: " +
      player.stats.map { case (key, value) => key.getAbbreviation + " %1.3f".format(value) }.mkString(" | ")
    }.mkString("\n")
    println(
     s"""
      |Lineup:
      |Score: $score
      |Players:
      |$players
      """.stripMargin.trim
    )
  }
}
