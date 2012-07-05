package com.olchovy.cli

import util.Random
import com.olchovy._, domain._, io._, Statistic._


object Boot
{
  type Options = Map[Symbol, Any]

  def main(args: Array[String]) {
    val usage = "Usage: " + getClass.getName + " --strategy filename [--players filename] [--lineup filename]"

    try {
      if(args.size == 0) throw new RuntimeException(usage)

      val options = parseOptions(args)

      try {
        assert(options.contains('strategy))
      } catch {
        case _ ⇒ throw new RuntimeException("--strategy is a required argument\n\n" + usage)
      }

      try {
        assert(options.contains('players) || options.contains('lineup))
      } catch {
        case _ ⇒ throw new RuntimeException("one of --players, --lineup is required as an argument\n\n" + usage)
      }

      val strategy = options('strategy).asInstanceOf[Strategy]

      if(options.contains('players)) {
        val players = options('players).asInstanceOf[Seq[Player]]
        val solutions = strategy.execute(players)

        println("SOLUTIONS FOUND: %s\n".format(solutions.size))

        solutions.foreach { lineup =>
          println(lineup)
          println("----------------------------------------")
          println("HASHCODE: " + lineup.hashCode)
          println("----------------------------------------")
          println("SCORE: " + strategy.fitness(lineup))
          println("========================================")
          println("")
        }
      }

      if(options.contains('lineup)) {
        val lineup = options('lineup).asInstanceOf[Lineup]
        println(lineup)
        println("----------------------------------------")
        println("HASHCODE: " + lineup.hashCode)
        println("----------------------------------------")
        println("SCORE: " + strategy.fitness(lineup))
        println("========================================")
        println("")
      }

      sys.exit(0)
    } catch {
      case e: Exception ⇒ println(e.getMessage); sys.exit(1)
    }
  }

  private def parseOptions(args: Seq[String]): Options = nextOption(Map(), args.toList)

  private def nextOption(opts: Options, args: List[String]): Options = args match {
    case Nil ⇒ opts

    case "--strategy" :: "softball" :: tail ⇒
      nextOption(opts ++ Map('strategy → new DefaultSoftballStrategy), tail)

    case "--strategy" :: "baseball" :: tail ⇒
      nextOption(opts ++ Map('strategy → new DefaultBaseballStrategy), tail)

    case "--strategy" :: filename :: tail ⇒
      nextOption(opts ++ Map('strategy → StrategyJsonFormat.fromFile(filename)), tail)

    case "--players" :: filename :: tail if opts.contains('lineup) ⇒
      throw new RuntimeException("'players' and 'lineup' are mutually exclusive options")

    case "--players" :: filename :: tail ⇒
      nextOption(opts ++ Map('players → LineupJsonFormat.fromFile(filename).players), tail)

    case "--lineup" :: filename :: tail if opts.contains('players) ⇒
      throw new RuntimeException("'lineup' and 'players' are mutually exclusive options")

    case "--lineup" :: filename :: tail ⇒
      nextOption(opts ++ Map('lineup → LineupJsonFormat.fromFile(filename)), tail)

    case option :: _ ⇒ throw new RuntimeException("Unknown option: " + option)
  }
}

