package com.olchovy

import scala.collection.mutable.WrappedArray
import scala.reflect.ClassTag
import scala.util.Random

package object ga {
  def arrayShuffle[A : ClassTag](as: Array[A]): Array[A] = {
    val traversable = WrappedArray.make[A](as)
    Random.shuffle(traversable).toArray
  }
}
