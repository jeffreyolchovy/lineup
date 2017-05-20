package com.olchovy.lineup

import org.scalatest.FlatSpec

class PlayerFitnessFunctionParserSpec extends FlatSpec {

  behavior of "PlayerFitnessFunctionParser"

  it should "parse empty string (no-op)" in {
    val input = ""
    parse(input)
  }

  it should "parse valid input with one statistic" in {
    val input = "+OBA"
    parse(input)
  }

  it should "parse valid input with multiple statistics" in {
    val input = "+OBA -PIP -2B"
    parse(input)
  }

  it should "parse valid input with statistics that contain special characters" in {
    val input = "+H -HR/H +3B -OBA +AVG"
    parse(input)
  }

  it should "parse valid input with no whitespace" in {
    val input = "+H-HR/H+3B-OBA+AVG"
    parse(input)
  }

  it should "fail to parse invalid input" in {
    val input = "foo"
    intercept[RuntimeException] {
      parse(input)
    }
  }

  it should "fail to parse invalid input ending with valid input" in {
    val input = "foo +PIP"
    intercept[RuntimeException] {
      parse(input)
    }
  }

  it should "fail to parse invalid input beginning with valid input" in {
    val input = "-SLG bar"
    intercept[RuntimeException] {
      parse(input)
    }
  }

  private def parse(input: String) = {
    PlayerFitnessFunctionParser(input)
  }
}
