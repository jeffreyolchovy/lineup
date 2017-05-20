package com.olchovy.ga

import org.slf4j.LoggerFactory

case class GeneticAlgorithm[A](
  initializer: Initializer[A],
  selector: Selector[A],
  operator: Operator[A],
  terminator: Terminator[A]) {

  import GeneticAlgorithm._

  def apply(): Set[A] = {
    log.info(s"Generating initial population using $initializer")
    val initPopulation = initializer.initialize()
    var state = State[A](
      initialPopulationSize = initPopulation.size,
      currentPopulation = initPopulation,
      currentGeneration = 0
    )
    while (hasNext(state)) {
      state = next(state)
    }
    state.currentPopulation
  }

  private def hasNext(state: State[A]): Boolean = {
    terminator(state) match {
      case Terminator.Terminate =>
        log.info("Terminating algorithm execution")
        false
      case Terminator.Continue =>
        log.info(s"Executing algorithm (generation ${state.currentGeneration})")
        true
    }
  }

  private def next(state: State[A]): State[A] = {
    val nextPopulation = (operator.apply _ andThen selector.apply _)(state.currentPopulation)
    state.copy(
      currentPopulation = nextPopulation,
      currentGeneration = state.currentGeneration + 1
    )
  }
}

object GeneticAlgorithm {

  private val log = LoggerFactory.getLogger(getClass)

  case class State[A](
    initialPopulationSize: Int,
    currentPopulation: Set[A],
    currentGeneration: Int
  )

  object State {
    def empty[A] = State(0, Set.empty, 0)
  }

  def newBuilder[A]: Builder[A] = new Builder[A]

  final class Builder[A](
    private[this] var initializer: Initializer[A] = null,
    private[this] var selector: Selector[A] = null,
    private[this] var operator: Operator[A] = null,
    private[this] var terminator: Terminator[A] = null) {

    def withInitializer(initializer: Initializer[A]): Builder[A] = {
      this.initializer = initializer
      this
    }

    def withSelector(selector: Selector[A]): Builder[A] = {
      this.selector = selector
      this
    }

    def withOperator(operator: Operator[A]): Builder[A] = {
      this.operator = operator
      this
    }

    def withTerminator(terminator: Terminator[A]): Builder[A] = {
      this.terminator = terminator
      this
    }

    def validate(): Unit = {
      require(initializer != null)
      require(selector != null)
      require(operator != null)
      require(terminator != null)
    }

    def build(): GeneticAlgorithm[A] = {
      validate()
      GeneticAlgorithm(
        initializer,
        selector,
        operator,
        terminator
      )
    }
  }
}
