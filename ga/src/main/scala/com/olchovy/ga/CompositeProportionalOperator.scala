package com.olchovy.ga

case class CompositeProportionalOperator[A : Numeric, B](
  private val weightedOperator1: (A, Operator[B]),
  private val additionalWeightedOperators: (A, Operator[B])*
) extends Operator[B] {

  private val proportionalOperators = ProportionalCollection[A, Operator[B]](
    weightedOperator1,
    additionalWeightedOperators: _*
  )

  private val proportionalOperatorIterator = proportionalOperators.iterator

  def apply(population: Set[B]): Set[B] = {
    val operator = proportionalOperatorIterator.next()
    operator(population)
  }
}
