package com.olchovy.lineup
package serde

import org.json4s._
import org.json4s.native.JsonMethods.parse
import org.json4s.native.Serialization
import org.json4s.native.Serialization.{read, write}
import org.scalatest.{FlatSpec, Matchers}

class PlayerJsonSerializationSpec extends FlatSpec with Matchers {

  import Statistic._

  implicit val format = DefaultFormats

  behavior of "PlayerJsonSerialization"

  it should "unmarshal a JSON value as an instance of a Player" in {
    val jsonString =
      """
      { "name" : "Foo"
      , "statistics" :
        { "AB" : 10
        , "H" : 3
        , "2B" : 1
        , "BB" : 1
        , "SF" : 2
        }
      }
      """
    val json = parse(jsonString)
    val result = PlayerJsonSerialization.deserialize(json)
    result should not be empty
    val player = result.get
    player.name shouldBe "Foo"
    player.stats(AT_BATS) shouldBe 10.0
    player.stats(HITS) shouldBe 3.0
    player.stats(BATTING_AVG) shouldBe (3.0 / 10.0)
  }

  it should "fail when given a JSON value with unknown values" in {
    {
      val jsonString =
        """
        { "foo" : "Foo"
        , "statistics" :
          { "AB" : 10 , "H" : 3 }
        }
        """
      val json = parse(jsonString)
      val result = PlayerJsonSerialization.deserialize(json)
      result shouldBe empty 
    }
    {
      val jsonString =
        """
        { "name" : "Foo"
        , "statistics" :
          { "FOO" : 10 , "H" : 3 }
        }
        """
      val json = parse(jsonString)
      val result = PlayerJsonSerialization.deserialize(json)
      result shouldBe empty
    }
  }

  it should "marshal a Player instance to a JSON object" in {
    val player = Player("Foo", AT_BATS -> 10, HITS -> 3, DOUBLES -> 1, WALKS -> 1)
    val json = PlayerJsonSerialization.serialize(player)
    val name = (json \ "name").extract[String]
    name shouldBe "Foo"
    val stats = (json \ "statistics").extract[Map[String, Double]]
    stats("AB") shouldBe 10D
    stats("H") shouldBe 3D
    stats("AVG") shouldBe (3D / 10D)
  }

  it should "allow Lineup instances to be (de)serialized to/from JSON" in {
    implicit val format = Serialization.formats(NoTypeHints) + PlayerJsonSerialization.serializer
    val lineup = Lineup(
      Player("Foo", AT_BATS -> 10, HITS -> 3, DOUBLES -> 1, WALKS -> 1, SACRIFICE_FLYOUTS -> 2),
      Player("Bar", AT_BATS -> 12, HITS -> 5, TRIPLES -> 1, HOMERUNS -> 1, WALKS -> 2, STRIKEOUTS -> 1)
    )
    read[Lineup](write(lineup)) shouldBe lineup
  }
}
