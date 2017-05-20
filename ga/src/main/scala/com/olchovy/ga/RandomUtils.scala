package com.olchovy.ga

import scala.collection.mutable.WrappedArray
import scala.reflect.ClassTag
import scala.util.Random

object RandomUtils {

  def arrayShuffle[A : ClassTag](as: Array[A]): Array[A] = {
    val traversable = WrappedArray.make[A](as.clone)
    Random.shuffle(traversable).toArray
  }

  def randomIndex(xs: Array[_], ys: Int*): Int = {
    val domain = (0 until xs.size).toSet
    val exclusions = Set(ys: _*)
    randomFrom(domain, exclusions)
  }

  @annotation.tailrec
  def randomFrom[A](xs: Set[A], exclusions: Set[A] = Set.empty): A = {
    val i = Random.nextInt(xs.size)
    val x = xs.toSeq.apply(i)

    require(!xs.subsetOf(exclusions), "All available elements are excluded from selection")

    if (exclusions.contains(x)) {
      randomFrom(xs, exclusions)
    } else {
      x
    }
  }
}
