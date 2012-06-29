package com.olchovy.cli

import util.Random
import com.olchovy.domain._, Statistic._


object Boot
{
  def fromFile(filename: String): Lineup = {
    import scala.io.Source
    import scala.collection.immutable.ListMap 


    val lines = Source.fromURL(getClass.getResource(filename)).getLines.toList
    val columns = lines.head.split(",").toList
    val rows = lines.tail.map(line => ListMap(columns.zip(line.split(",")): _*))
    val players = rows.map { row =>
      val name = row("name")
      val stats = Map(
        G → row("G").toDouble,
        AB → row("AB").toDouble,
        H → row("H").toDouble,
        `2B` → row("2B").toDouble,
        `3B` → row("3B").toDouble,
        HR → row("HR").toDouble,
        RBI → row("RBI").toDouble,
        R → row("R").toDouble,
        BB → row("BB").toDouble,
        SO → row("SO").toDouble,
        SF → row("SF").toDouble,
        E → row("E").toDouble
      )

      Player(name, stats)
    }

    Lineup(players)
  }

  def main(args: Array[String]) {
    import com.olchovy.DefaultStrategy


    val strategy = new DefaultStrategy
    val lineup = fromFile("/stats.csv")
    val solutions = strategy.execute(lineup)

    println("SOLUTIONS FOUND: %s\n".format(solutions.size))

    solutions.foreach { l =>
      println(l)
      println("----------------------------------------")
      println("HASHCODE: " + l.hashCode)
      println("----------------------------------------")
      println("SCORE: " + strategy.fitness(l))
      println("========================================")
      println("")
    }
  }
}

