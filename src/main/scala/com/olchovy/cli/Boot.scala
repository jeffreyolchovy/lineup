package com.olchovy.cli

import util.Random
import com.olchovy.domain._


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
      val position = row.get("position").filter(!_.isEmpty)
      val G = row("G").toInt
      val AB = row("AB").toInt
      val H = row("H").toInt
      val `2B` = row("2B").toInt
      val `3B` = row("3B").toInt
      val HR = row("HR").toInt
      val RBI = row("RBI").toInt
      val R = row("R").toInt
      val BB = row("BB").toInt
      val SO = row("SO").toInt
      val SF = row("SF").toInt
      val E = row("E").toInt

      Player(name, position, Player.Statistics(G, AB, H, `2B`, `3B`, HR, RBI, R, BB, SO, SF, E))
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

