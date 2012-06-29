package com.olchovy.dsl

import util.matching.Regex
import util.parsing.combinator.RegexParsers
import com.olchovy.domain._


object FitnessSpecificationParser extends RegexParsers
{
  private case class Token(sign: String, statistic: Statistic.Value)

  private lazy val token = sign ~ statistic ^^ { case sign ~ statistic ⇒ Token(sign, statistic) }

  private lazy val sign = ( "+" | "-" )

  private lazy val statistic = """\w+""".r ^? ({
    case str if Statistic.values.exists(abbr ⇒ abbr.toString == str) ⇒ Statistic.withName(str)
  }, "Unknown statistic abbreviation: " + _)

  lazy val specification: Parser[(Lineup, Player) => Double] = (token +) ^^ { tokens ⇒
    val functions = tokens.map { case Token(sign, statistic) ⇒
      (lineup: Lineup, player: Player) ⇒ sign match {
        case "+" ⇒ player.stat(statistic) - lineup.stat(statistic)
        case "-" ⇒ lineup.stat(statistic) - player.stat(statistic)
      }
    }

    (lineup: Lineup, player: Player) ⇒ functions.map(_.apply(lineup, player)).sum
  }

  def main(args: Array[String]) {
    val attempt = args.mkString(" ")

    println("Attempting to parse: " + attempt)

    parse(specification, attempt) match {
      case Success(tree, _) ⇒
        println(tree.toString)

      case e: NoSuccess ⇒
        println(e.toString)
    }
  }
}

