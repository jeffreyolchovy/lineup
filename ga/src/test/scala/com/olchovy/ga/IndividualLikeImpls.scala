package com.olchovy.ga

import scala.reflect.ClassTag

object IndividualLikeImpls {

  def individualLikeSeq[A : ClassTag, B : Numeric : Ordering](f: FitnessFunction[Seq[A], B]): IndividualLike[Seq[A], A, B] = {
    new IndividualLike[Seq[A], A, B] {
      val fitnessNumeric = implicitly[Numeric[B]]
      val fitnessOrdering = implicitly[Ordering[B]]
      val fitness = f

      def getChromosomes(seq: Seq[A]): Array[A] = {
        seq.toArray.clone
      }

      def fromChromosomes(chromosomes: Array[A]): Seq[A] = {
        chromosomes.clone.toSeq
      }
    }
  }

  def individualLikeBitSeq: IndividualLike[Seq[Boolean], Boolean, Double]= {
    individualLikeSeq[Boolean, Double] { bits =>
      bits.map(if (_) 1D else 0D).sum / bits.size
    }
  }

  def individualLikeIntSeq: IndividualLike[Seq[Int], Int, Int]= {
    individualLikeSeq[Int, Int] { ints =>
      (for {
        (int, i) <- ints.zipWithIndex
      } yield {
        if (i == int) 1 else 0
      }).sum
    }
  }
}
