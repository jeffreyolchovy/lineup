package com.olchovy.dsl

import util.matching.Regex
import util.parsing.combinator.RegexParsers
import com.olchovy.domain._


object FitnessSpecificationParser extends RegexParsers
{
  private case class Token(sign: String, statistic: Statistic.Value)

  private lazy val EOL = """\z""".r

  private lazy val sign = ( "+" | "-" )

  private lazy val statistic = """[\w|/]+""".r ^? ({
    case str if Statistic.values.exists(abbr ⇒ abbr.toString == str) ⇒ Statistic.withName(str)
  }, "Unknown statistic abbreviation: " + _)

  private lazy val token = sign ~ statistic ^^ {
    case sign ~ statistic ⇒ Token(sign, statistic)
  }

  private lazy val specification: Parser[(Lineup, Player) ⇒ Double] = (token +) ~ EOL ^^ {
    case tokens ~ _ ⇒
      val functions = tokens.map { case Token(sign, statistic) ⇒
        (lineup: Lineup, player: Player) ⇒ sign match {
          case "+" ⇒ player.stat(statistic) - lineup.stat(statistic)
          case "-" ⇒ lineup.stat(statistic) - player.stat(statistic)
        }
      }

      (lineup: Lineup, player: Player) ⇒ functions.map(_.apply(lineup, player)).sum
  }

  def apply(input: String): (Lineup, Player) ⇒ Double = parse(specification, input) match {
    case Success(result, _) ⇒ result
    case e: NoSuccess ⇒ throw new RuntimeException(e.toString)
  }
}

