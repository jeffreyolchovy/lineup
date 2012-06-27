package com.olchovy.domain


case class Player(name: String, position: Option[String], statistics: Player.Statistics)
{
  override def toString = name

  override def equals(that: Any): Boolean = that match {
    case player: Player ⇒ name == player.name
    case _ => false
  }

  override def hashCode = name.toUpperCase.hashCode
}

object Player
{
  def apply(_name: String, _statistics: Player.Statistics): Player = {
    Player(_name, None, _statistics)
  }

  def apply(_name: String, _position: String, _statistics: Player.Statistics): Player = {
    Player(_name, Some(_position), _statistics)
  }

  case class Statistics
  (
      G : Int, // games
     AB : Int, // at-bats
      H : Int, // hits
   `2B` : Int, // doubles
   `3B` : Int, // triples
     HR : Int, // home runs
    RBI : Int, // runs batted-in
      R : Int, // runs
     BB : Int, // walks
     SO : Int, // strike outs
     SF : Int, // sacrifice fly-outs
      E : Int  // errors
  )
  {
    /* singles */
    lazy val `1B`: Int = H - (`2B` + `3B` + HR) 

    /* batting average */
    lazy val AVG: Double = H / (AB: Double)

    /* slugging average */
    lazy val SLG: Double = (`1B` + (2 * `2B`) + (3 * `3B`) + (4 * HR)) / (AB: Double)

    /* on-base average */
    lazy val OBA: Double = (H + BB) / ((AB: Double) + BB + SF)

    /* extra-base average */
    lazy val EBA: Double = ((`2B` + 2) * (`3B` + 3) * HR + BB) / (AB: Double) 

    /* put-in-play percentage */
    lazy val PIP: Double = 1 - (SO / (AB: Double))

    lazy val `HR/H`: Double = HR / (H: Double)

    lazy val `BB/AB`: Double = BB / (AB: Double)

    lazy val `SO/AB`: Double = SO / (AB: Double)

    lazy val `1B/H`: Double = `1B` / (H: Double)
  }
}
