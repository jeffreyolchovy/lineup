package com.olchovy.ga

import org.slf4j.LoggerFactory

case class GeneticAlgorithm[A](
  initializer: Initializer[A],
  selector: Selector[A],
  operator: Operator[A],
  terminator: Terminator[A]) {

  import GeneticAlgorithm._

  private val log = LoggerFactory.getLogger(getClass)

  def apply(): Set[A] = {
    log.info(s"Generating initial population using $initializer")
    val initPopulation = initializer.initialize()
    var state = State[A](
      initialPopulationSize = initPopulation.size,
      currentPopulation = initPopulation,
      currentGeneration = 0
    )
    log.info("Executing algorithm...")
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
        log.debug(s"Executing algorithm (generation ${state.currentGeneration})")
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

  case class State[A](
    initialPopulationSize: Int,
    currentPopulation: Set[A],
    currentGeneration: Int
  )

  object State {
    def empty[A] = State(0, Set.empty, 0)
  }
}
