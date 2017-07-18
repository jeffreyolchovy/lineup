package com.olchovy.lineup
package serde

import org.json4s._
import org.json4s.native.JsonMethods.parse
import org.scalatest.{FlatSpec, Matchers}
import com.olchovy.ga.{Operator, Selector}

class GeneticAlgorithmJsonSerializationSpec extends FlatSpec with Matchers {

  implicit val format = DefaultFormats + PlayerJsonSerialization.serializer

  implicit val individualLike = Lineup.IndividualLikeLineup

  behavior of "GeneticAlgorithmJsonSerialization"

  it should "unmarshal JSON values as an instance of a GeneticAlgorithm" in {
    val jsonString =
      """
      { "chromosomes" :
        [
          { "name" : "Foo"
          , "statistics" :
            { "AB" : 10
            , "H" : 3
            , "2B" : 1
            , "BB" : 1
            , "SF" : 2
            }
          }
        ]
      , "selectors" :
        [
          { "weight" : 1 , "type" : "top_n" , "features" : { "n" : 0.1 } }
        ]
      , "operators" :
        [
          { "weight" : 3 , "type" : "crossover" }
        ,
          { "weight" : 1 , "type" : "mutation" }
        ]
      }
      """
    val json = parse(jsonString)
    val serializer = new GeneticAlgorithmJsonSerialization
    val result = serializer.deserialize(json)
    result should not be empty
  }

  it should "fail if chromosomes omitted or empty" in {
    val jsonString =
      """
      { "selectors" :
        [
          { "weight" : 1 , "type" : "top_n" , "features" : { "n" : 0.1 } }
        ]
      , "operators" :
        [
          { "weight" : 1 , "type" : "mutation" }
        ]
      }
      """
    val json = parse(jsonString)
    val serializer = new GeneticAlgorithmJsonSerialization
    val result = serializer.deserialize(json)
    result shouldBe empty
  }


  it should "fail if selectors is empty" in {
    val jsonString =
      """
      { "chromosomes" : []
      , "selectors" : []
      , "operators" :
        [
          { "weight" : 1 , "type" : "mutation" }
        ]
      }
      """
    val json = parse(jsonString)
    val serializer = new GeneticAlgorithmJsonSerialization
    val result = serializer.deserialize(json)
    result shouldBe empty
  }

  it should "fail if operators is empty" in {
    val jsonString =
      """
      { "chromosomes" : []
      , "selectors" :
        [
          { "weight" : 1 , "type" : "top_n" , "features" : { "n" : 0.1 } }
        ]
      , "operators" : []
      }
      """
    val json = parse(jsonString)
    val serializer = new GeneticAlgorithmJsonSerialization
    val result = serializer.deserialize(json)
    result shouldBe empty
  }
}
