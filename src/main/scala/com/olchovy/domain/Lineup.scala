package com.olchovy.domain


case class Lineup(players: Seq[Player])
{
  private def mean(f: Player => Double): Double = players.map(f).sum / size

  /* batting average */
  def AVG: Double = mean(_.statistics.AVG)

  /* slugging average */
  def SLG: Double = mean(_.statistics.SLG)

  /* on-base average */
  def OBA: Double = mean(_.statistics.OBA)

  /* extra-base average */
  def EBA: Double = mean(_.statistics.EBA)

  /* put-in-play percentage */
  def PIP: Double = mean(_.statistics.PIP)

  def `HR/H`: Double = mean(_.statistics.`HR/H`)

  def `BB/AB`: Double = mean(_.statistics.`BB/AB`)

  def `SO/AB`: Double = mean(_.statistics.`SO/AB`)

  def `1B/H`: Double = mean(_.statistics.`1B/H`)

  def size = players.size

  override def toString = players.zipWithIndex.map { case (player, i) ⇒ "[%s] %s".format(i + 1, player) }.mkString("\n")

  override def equals(that: Any): Boolean = that match {
    case lineup: Lineup ⇒ players == lineup.players
    case _ => false
  }

  override def hashCode = players.hashCode
}

