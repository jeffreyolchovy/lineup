package com.olchovy.ga

trait Terminator[A] extends (GeneticAlgorithm.State[A] => Terminator.Result)

object Terminator {

  sealed trait Result
  case object Continue extends Result
  case object Terminate extends Result

  /** A solution is found that satisfies minimum criteria */
  def criteriaBased[A](f: GeneticAlgorithm.State[A] => Boolean): Terminator[A] = new Terminator[A] {
    def apply(state: GeneticAlgorithm.State[A]): Result = {
      if (f(state)) {
        Terminate
      } else {
        Continue
      }
    }
  }

  /** A fixed number of generations has been reached */
  def generationBased[A](maxGenerations: Int): Terminator[A] = new Terminator[A] {
    def apply(state: GeneticAlgorithm.State[A]): Result = {
      if (state.currentGeneration < maxGenerations) {
        Continue
      } else {
        Terminate
      }
    }
  }

  /** An allocated budget (computation time/money) has been reached */
  /*
  class BudgetBased[A : IndividualLike] extends Terminator[A] {
    def apply(state: GeneticAlgorithm.State[A]): Result = {
      ???
    }
  }
  */

  /** The highest ranking solution's fitness is reaching or has reached a plateau
    * such that successive iterations no longer produce better results
    */
  /*
  class GrowthBased[A : IndividualLike] extends Terminator[A] {
    def apply(state: GeneticAlgorithm.State[A]): Result = {
      ???
    }
  }
  */

  /** Terminate based on manual inspection/intervention */
  /*
  class InterruptBased[A : IndividualLike] extends Terminator[A] {
    def apply(state: GeneticAlgorithm.State[A]): Result = {
      ???
    }
  }
  */
}
