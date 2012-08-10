package com.olchovy.lineup.io

import org.scalatest.FeatureSpec
import org.scalatest.GivenWhenThen
import cc.spray.json._, DefaultJsonProtocol._
import com.olchovy.lineup.domain._
import com.olchovy.lineup.{Strategy, DefaultStrategy, DefaultBaseballStrategy}


class StrategyJsonFormatSpec extends FeatureSpec with GivenWhenThen
{
  import Statistic._

  feature("Strategy instances cannot be marshaled to JSON") {
    scenario("marshal a Strategy instance to JSON") {
      given("a Strategy instance") 
      val strategy = new DefaultBaseballStrategy

      when("'write' is invoked on the StrategyJsonFormat")
      then("a SerializationException should be thrown")
      intercept[SerializationException] {
        StrategyJsonFormat.write(strategy)
      }
    }
  }

  feature("Strategy instances can be unmarshaled from JSON") {
    scenario("unmarshal a JSON string into a Strategy instance") {
      given("a JSON string")
      val jsonString = """
      { "initial_population_size" : 2500
      , "initial_fitness_threshold" : 1.0
      , "maximum_fitness_threshold" : 4.0
      , "maximum_generations" : 100
      , "fitness_specification" :
        [ ["+OBA", "-PIP"]
        , ["+OBA", "+SLG"]
        ]
      }
      """

      when("'read' is invoked on StrategyJsonFormat")
      val json = JsonParser(jsonString)
      val strategy = StrategyJsonFormat.read(json)

      then("a Strategy instance should be returned")
      assert(strategy.isInstanceOf[Strategy])

      and("it should have the values present in the original JSON string")
      assert(strategy.LINEUP_SIZE == 2)
      assert(strategy.INITIAL_POPULATION_SIZE == 2500)
      assert(strategy.INITIAL_FITNESS_THRESHOLD == 1.0)
      assert(strategy.MAX_FITNESS_THRESHOLD == 4.0)
      assert(strategy.MAX_GENERATIONS == 100)
    }
  }
}

