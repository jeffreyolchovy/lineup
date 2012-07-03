package com.olchovy.io

import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import cc.spray.json._, DefaultJsonProtocol._
import com.olchovy.domain._, Statistic._


class LineupJsonFormatSpec extends FeatureSpec with GivenWhenThen
{
  feature("Lineup instances can be marshaled to JSON") {
    scenario("marshal a Lineup instance to JSON") {
      given("a Lineup instance")
      val lineup = Lineup(Seq(
        Player("Foo", Map(AB → 10, H → 3, `2B` → 1, BB → 1, SF → 2)),
        Player("Bar", Map(AB → 12, H → 5, `3B` → 1, HR → 1, BB → 2, SO → 1))
      ))

      when("the Lineup instance is written as JSON")
      val json = LineupJsonFormat.write(lineup)

      then("a JSON array should be returned")
      assert(json.isInstanceOf[JsArray])

      and("it should contain elements for each Player instance")
      assert(json.asInstanceOf[JsArray].elements.size == 2)
    }
  }

  feature("Lineup instances can be unmarshaled from JSON") {
    scenario("unmarshal a JSON string as an instance of a Lineup") {
      given("a JSON string")
      val jsonString = """
      [
        { "name" : "Foo"
        , "stats" :
          { "AB" : 10
          , "H" : 3
          , "2B" : 1
          , "BB" : 1
          , "SF" : 2
          }
        }
      , { "name" : "Bar"
        , "stats" : 
          { "AB" : 12 
          , "H" : 5
          , "3B" : 1
          , "HR" : 1
          , "BB" : 2
          , "SO" : 1
          }
        }
      ]
      """

      when("the string is parsed and read by the JsonFormat")
      val json = JsonParser(jsonString)
      val lineup = LineupJsonFormat.read(json)

      then("a Lineup instance should be returned")
      assert(lineup.isInstanceOf[Lineup])

      and("each player should have the values present in the original JSON string")
      assert(lineup.players(0).name == "Foo")
      assert(lineup.players(0).stat(AB) == 10.0)
      assert(lineup.players(0).stat(H) == 3.0)
      assert(lineup.players(0).stat(AVG) == (3.0 / 10.0))
      assert(lineup.players(1).name == "Bar")
      assert(lineup.players(1).stat(AB) == 12.0)
      assert(lineup.players(1).stat(H) == 5.0)
      assert(lineup.players(1).stat(`HR/H`) == (1.0 / 5.0))
    }
  }
}

