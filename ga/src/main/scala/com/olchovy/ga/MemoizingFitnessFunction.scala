package com.olchovy.ga

class MemoizingFitnessFunction[A, B](underlying: FitnessFunction[A, B]) extends FitnessFunction[A, B] {
  def apply(a: A): B = {
    ???
  }
}
