package com.olchovy.lineup.cli

import java.io.File
import scala.io.Source
import scala.util.Random
import cc.spray.json.{JsonParser, RootJsonFormat}
import com.olchovy.lineup.api.RestService
import com.olchovy.lineup.domain._
import com.olchovy.lineup.io._

object Boot {

  type Options = Map[Symbol, Any]

  def main(args: Array[String]) {
    val usage =
      """
      |usage: %s [options]
      |
      |options:
      | --api         run an embedded REST API server
      | --strategy <path> [--players <path>] [--lineup <path>]
      |               generate lineups or calculate fitness score
      """.stripMargin.trim.format(getClass.getSimpleName)

    try {
      if (args.isEmpty)
        throw new RuntimeException(usage)

      val options = parseOptions(args)

      try {
        assert(options.contains('api) || options.contains('strategy))
      } catch {
        case _ ⇒ throw new RuntimeException("one of --api, --strategy is a required as an argument\n\n" + usage)
      }

      if (options.contains('api))
        RestService.run()
      else {
        val strategy = options('strategy).asInstanceOf[Strategy]

        try {
          assert(options.contains('players) || options.contains('lineup))
        } catch {
          case _ ⇒ throw new RuntimeException("one of --players, --lineup is required as an argument\n\n" + usage)
        }

        if (options.contains('players)) {
          val players = options('players).asInstanceOf[Seq[Player]]
          val lineups = strategy.execute(players)
          println("%d solution(s) found".format(lineups.size))
          printLineup(strategy, lineups.toSeq: _*)
        }

        if (options.contains('lineup)) {
          val lineup = options('lineup).asInstanceOf[Lineup]
          printLineup(strategy, lineup)
        }

        sys.exit(0)
      }
    } catch {
      case e: RuntimeException ⇒
        println(e.getMessage)
        sys.exit(1)

      case e: Exception ⇒
        e.printStackTrace
        sys.exit(2)
    }
  }
  
  def parseOptions(args: Seq[String]): Options = {
    def helper(opts: Options, args: List[String]): Options = args match {
      case Nil ⇒ opts

      case "--api" :: tail ⇒
        helper(opts ++ Map('api → true), tail)

      case "--strategy" :: "softball" :: tail ⇒
        helper(opts ++ Map('strategy → DefaultSoftballStrategy), tail)

      case "--strategy" :: "baseball" :: tail ⇒
        helper(opts ++ Map('strategy → DefaultBaseballStrategy), tail)

      case "--strategy" :: path :: tail ⇒
        parseStrategyFile(new File(path)).fold(
          failure => throw failure,
          success => helper(opts ++ Map('strategy → success), tail)
        )

      case "--players" :: _ :: tail if opts.contains('lineup) ⇒
        throw new RuntimeException("'players' and 'lineup' are mutually exclusive options")

      case "--players" :: path :: tail ⇒
        parseLineupFile(new File(path)).fold(
          failure => throw failure,
          success => helper(opts ++ Map('players → success.players), tail)
        )

      case "--lineup" :: _ :: tail if opts.contains('players) ⇒
        throw new RuntimeException("'lineup' and 'players' are mutually exclusive options")

      case "--lineup" :: path :: tail ⇒
        parseLineupFile(new File(path)).fold(
          failure => throw failure,
          success => helper(opts ++ Map('lineup → success), tail)
        )

      case option :: _ ⇒ throw new RuntimeException("Unknown option: " + option)
    }

    helper(Map(), args.toList)
  }

  def parseStrategyFile(file: File) = parseJsonFile(StrategyJsonFormat)(file)

  def parseLineupFile(file: File) = parseJsonFile(LineupJsonFormat)(file)

  def parseJsonFile[A](jsonFormat: RootJsonFormat[A])(file: File): Either[Exception, A] = {
    if (!file.exists)
      Left(new RuntimeException("File does not exist: " + file.getAbsolutePath))
    else {
      val contents = Source.fromFile(file.getAbsolutePath).mkString

      try {
        Right(jsonFormat.read(JsonParser(contents)))
      } catch {
        case e: Exception ⇒ Left(e)
      }
    }
  }

  def printLineup(strategy: Strategy, lineups: Lineup*) {
    lineups.foreach { lineup ⇒
      println(
        """
        |%s
        |----------------------------------------
        |hashcode: %s
        |----------------------------------------
        |score: %f
        |========================================
        """
        .stripMargin
        .trim
        .format(
          lineup,
          lineup.hashCode,
          strategy.fitness(lineup)
        )
      )
    }
  }
}
