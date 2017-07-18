package com.olchovy.lineup

import scala.util.matching.Regex
import scala.util.parsing.combinator.RegexParsers
import PlayerFitnessFunction._

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

  private val EOL = """\z""".r

  private val sign = ( "+" | "-" )

  private val statistic: Parser[Statistic] = {
    """[\w|/]+""".r ^? ({
      case str if Statistic.values.map(_.getAbbreviation).contains(str) => Statistic.fromAbbreviation(str)
    }, "Unknown statistic abbreviation: " + _)
  }

  private val rule: Parser[Rule] = {
    sign ~ statistic ^? ({
      case "+" ~ stat => Rule(BetterThanAvg, stat)
      case "-" ~ stat => Rule(WorseThanAvg, stat)
    }, "Unknown statistic specification: " + _)
  }

  private val fitnessF: Parser[PlayerFitnessFunction] = {
    (rule *) <~ EOL ^^ {
      case rules => PlayerFitnessFunction(rules: _*)
    }
  }

  def apply(input: String): PlayerFitnessFunction = {
    parse(fitnessF, input) match {
      case Success(result, _) => result
      case e: NoSuccess => throw new RuntimeException(e.toString)
    }
  }
}
