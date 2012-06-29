package com.olchovy.domain

import Statistic._


case class Player(name: String, private val _stats: Map[Statistic.Value, Double])
{
  lazy val stats: Map[Statistic.Value, Double] = _stats ++ Map(
    `1B` → _1B,
    AVG → _AVG,
    SLG → _SLG,
    OBA → _OBA,
    EBA → _EBA,
    PIP → _PIP,
    `HR/H`  → `_HR/H`,
    `BB/AB` → `_BB/AB`,
    `SO/AB` → `_SO/AB`,
    `1B/H`  → `_1B/H`
  )

  private lazy val _1B = _stats(H) - (_stats(`2B`) + _stats(`3B`) + _stats(HR))

  private lazy val _AVG = _stats(H) / _stats(AB)

  private lazy val _SLG = (_1B + (2 * _stats(`2B`)) + (3 * _stats(`3B`)) + (4 * _stats(HR))) / _stats(AB)
  
  private lazy val _OBA = (_stats(H) + _stats(BB)) / (_stats(AB) + _stats(BB) + _stats(SF))

  private lazy val _EBA = ((_stats(`2B`) + 2) * (_stats(`3B`) + 3) * _stats(HR) + _stats(BB)) / _stats(AB)

  private lazy val _PIP = 1 - (_stats(SO) / _stats(AB))

  private lazy val `_HR/H` = _stats(HR) / _stats(H)

  private lazy val `_BB/AB` = _stats(BB) / _stats(AB)

  private lazy val `_SO/AB` = _stats(SO) / _stats(AB)

  private lazy val `_1B/H` = _1B / _stats(H)

  def stat(name: Statistic.Value): Double = stats(name)

  override def toString = name

  override def equals(that: Any): Boolean = that match {
    case player: Player ⇒ name == player.name
    case _ => false
  }

  override def hashCode = name.toUpperCase.hashCode
}

