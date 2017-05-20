package com.olchovy.ga

import java.util.TreeMap
import scala.util.Random

case class ProportionalCollection[A : Numeric, B](
  private val weightedElem: (A, B),
  private val additionalWeightedElems: (A, B)*
) extends Iterable[B] {

  val weightedElems: Iterable[(A, B)] = weightedElem +: additionalWeightedElems

  val elems: Iterable[B] = weightedElems.map(_._2)

  private val (totalWeight: Double, navigableMap: TreeMap[Double, B]) = {
    weightedElems.foldLeft(0D -> new TreeMap[Double, B]) {
      case ((currentWeight, map), (a, b)) =>
        val nextWeight = currentWeight + implicitly[Numeric[A]].toDouble(a)
        map.put(nextWeight, b)
        nextWeight -> map
    }
  }

  def iterator: Iterator[B] = {
    new Iterator[B] {
      val hasNext = true
      def next(): B = {
        navigableMap.higherEntry(Random.nextDouble * totalWeight).getValue()
      }
    }
  }
}
