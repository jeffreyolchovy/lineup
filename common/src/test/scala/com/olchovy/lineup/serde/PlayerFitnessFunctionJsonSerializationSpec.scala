package com.olchovy.lineup
package serde

import org.json4s._
import org.json4s.native.JsonMethods.parse
import org.scalatest.{FlatSpec, Matchers}

class PlayerFitnessFunctionJsonSerializationSpec extends FlatSpec with Matchers {

  import Statistic._

  implicit val format = DefaultFormats

  behavior of "PlayerFitnessFunctionJsonSerialization"

  it should "unmarshal JSON values as an instance of a fitness function" in {
    runDeserializationTest(JString("+OBA -ISO"), "+OBA -ISO")
    runDeserializationTest(parse("""["+OBA", "-ISO"]"""), "+OBA -ISO")
  }

  def runDeserializationTest(json: JValue, expectationString: String): Unit = {
    val result = PlayerFitnessFunctionJsonSerialization.deserialize(json)
    result should not be empty
    val fitnessF = result.get
    val expectation = PlayerFitnessFunctionParser(expectationString)
    fitnessF shouldBe expectation
  }
}
