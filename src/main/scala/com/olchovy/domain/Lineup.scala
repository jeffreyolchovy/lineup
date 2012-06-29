package com.olchovy.domain

import Statistic._


case class Lineup(players: Seq[Player])
{
  def size = players.size

  def stat(name: Statistic.Value): Double = players.map(_.stat(name)).sum / size

  override def toString = players.zipWithIndex.map { case (player, i) ⇒ "[%s] %s".format(i + 1, player) }.mkString("\n")

  override def equals(that: Any): Boolean = that match {
    case lineup: Lineup ⇒ players == lineup.players
    case _ => false
  }

  override def hashCode = players.hashCode
}
