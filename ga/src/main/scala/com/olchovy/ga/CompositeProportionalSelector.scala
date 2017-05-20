package com.olchovy.ga

case class CompositeProportionalSelector[A : Numeric, B](
  private val weightedSelector1: (A, Selector[B]),
  private val weightedSelector2: (A, Selector[B]),
  private val additionalWeightedSelectors: (A, Selector[B])*
) extends Selector[B] {

  private val proportionalSelectors = ProportionalCollection[A, Selector[B]](
    weightedSelector1,
    (weightedSelector2 +: additionalWeightedSelectors): _*
  )

  private val proportionalSelectorIterator = proportionalSelectors.iterator

  def apply(population: Set[B]): Set[B] = {
    val selector = proportionalSelectorIterator.next()
    selector(population)
  }
}
