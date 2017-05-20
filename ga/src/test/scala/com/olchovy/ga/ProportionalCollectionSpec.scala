package com.olchovy.ga

import org.scalatest.{FlatSpec, Matchers}

class ProportionalCollectionSpec extends FlatSpec with Matchers {

  behavior of "ProportionalCollection"

  it should "require at least one weighted element" in {
    {
      val xs = ProportionalCollection(1 -> "foo")
      xs.iterator.next() shouldBe "foo"
    }
    {
      val xs = ProportionalCollection(1 -> "foo", 2 -> "bar")
      val it = xs.iterator
      var fooCount = 0
      var barCount = 0
      for(_ <- 0 to 100) {
        it.next() match {
          case "foo" => fooCount += 1
          case "bar" => barCount += 1
        }
      }
      barCount should be > (fooCount)
    }
  }

  it should "allow weights of any numeric type" in {
    {
      val xs = ProportionalCollection(1 -> "foo")
    }
    {
      val xs = ProportionalCollection(0.25 -> "foo", 0.75 -> "bar")
    }
    {
      """val xs = ProportionalCollection("foo" -> "bar")""" shouldNot compile
    }
  }
}
