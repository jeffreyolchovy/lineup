package com.olchovy.io

import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import cc.spray.json._, DefaultJsonProtocol._
import com.olchovy.domain._, Statistic._


class PlayerJsonFormatSpec extends FeatureSpec with GivenWhenThen
{
  feature("Player instances can be marshaled to JSON") {
    scenario("marshal a Player instance to JSON") {
      given("a Player instance") 
      val player = Player("Foo", Map(AB → 10, H → 3, `2B` → 1, BB → 1, SF → 2))

      when("the Player instance is written as JSON")
      val json = PlayerJsonFormat.write(player)

      then("a JSON object should be returned")
      assert(json.isInstanceOf[JsObject])

      and("it should contain properties 'name' and 'stats'")
      val fields = json.asJsObject.getFields("name", "stats")
      assert(fields.size == 2)
      assert(fields(0).convertTo[String] == "Foo")
      assert(fields(1).asJsObject.fields("AB").convertTo[Double] == 10.0)
    }
  }

  feature("Player instances can be unmarshaled from JSON") {
    scenario("unmarshal a JSON string as an instance of a Player") {
      given("a JSON string")
      val jsonString = """
      { "name" : "Foo"
      , "stats" :
        { "AB" : 10
        , "H" : 3
        , "2B" : 1
        , "BB" : 1
        , "SF" : 2
        }
      }
      """

      when("the string is parsed and read by the JsonFormat")
      val json = JsonParser(jsonString)
      val player = PlayerJsonFormat.read(json)

      then("a Player instance should be returned")
      assert(player.isInstanceOf[Player])

      and("it should have the values present in the original JSON string")
      assert(player.name == "Foo")
      assert(player.stat(AB) == 10.0)
      assert(player.stat(H) == 3.0)
      assert(player.stat(AVG) == (3.0 / 10.0))
    }
  }
}

