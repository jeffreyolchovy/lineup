package com.olchovy.lineup

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers

/** A grammar for defining fitness functions for a Player in a given Lineup
  *
  * e.g. A string, such as <code>"+OBA -PIP"</code>, would produce a function whose output
  * would be equivalent to the execution of the following function:
  *
  *  <code>(a, b) ⇒ (b.stat(OBA) - a.stat(OBA)) + (a.stat(PIP) - b.stat(PIP))</code>
  *
  * Where <code>a</code> is a given Lineup and <code>b</code> is a Player at some index in <code>a</code>.
  */
object PlayerFitnessFunctionParser extends RegexParsers {

  private case class Token(sign: String, statistic: Statistic)

  private val EOL = """\z""".r

  private val sign = ( "+" | "-" )

  private val statistic: Parser[Statistic] = {
    """[\w|/]+""".r ^? ({
      case str if Statistic.values.map(_.getAbbreviation).contains(str) => Statistic.fromAbbreviation(str)
    }, "Unknown statistic abbreviation: " + _)
  }

  private val token: Parser[Token] = {
    sign ~ statistic ^^ {
      case sign ~ statistic => Token(sign, statistic)
    }
  }

  private val fitnessF: Parser[(Lineup, Player) ⇒ Double] = (token *) <~ EOL ^^ {
    case tokens =>
      val functions = tokens.map { case Token(sign, statistic) =>
        (lineup: Lineup, player: Player) => sign match {
          case "+" => player.stats(statistic) - lineup.stats(statistic)
          case "-" => lineup.stats(statistic) - player.stats(statistic)
        }
      }
      (lineup: Lineup, player: Player) => functions.map(_.apply(lineup, player)).sum
  }

  def apply(input: String): (Lineup, Player) ⇒ Double = parse(fitnessF, input) match {
    case Success(result, _) => result
    case e: NoSuccess => throw new RuntimeException(e.toString)
  }
}
