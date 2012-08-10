package com.olchovy.lineup.dsl

import org.scalatest.FunSuite


class FitnessSpecificationParserSuite extends FunSuite
{
  private def parse(string: String) = FitnessSpecificationParser(string)

  test("Parse empty string (no-op)") {
    val string = ""
    parse(string)
  }

  test("Parse valid string with one statistic") {
    val string = "+OBA"
    parse(string)
  }

  test("Parse valid string with multiple statistics") {
    val string = "+OBA -PIP -2B"
    parse(string)
  }

  test("Parse valid string with statistics that contain special characters") {
    val string = "+H -HR/H +3B -OBA +AVG"
    parse(string)
  }

  test("Parse valid string with no whitespace") {
    val string = "+H-HR/H+3B-OBA+AVG"
    parse(string)
  }
  
  test("Parse invalid string") {
    val string = "foo"
    intercept[RuntimeException] {
      parse(string)
    }
  }

  test("Parse invalid string ending with valid string") {
    val string = "foo +PIP"
    intercept[RuntimeException] {
      parse(string)
    }
  }

  test("Parse invalid string beginning with valid string") {
    val string = "-SLG bar"
    intercept[RuntimeException] {
      parse(string)
    }
  }
}

