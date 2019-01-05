package utils

import java.util.concurrent.ThreadLocalRandom

object WeightedRandomSampler {
  def apply[A](elements: Seq[A])(weightMap: A => Double) = {
    val weights = elements.map(weightMap)
    new WeightedRandomSampler(elements.zip(weights), weights.sum)
  }
}

class WeightedRandomSampler[A](val weightedSeq: Seq[(A, Double)], val weightSum: Double) {
  def sample: A = {
    val r = ThreadLocalRandom.current().nextDouble(0, weightSum)

    var currentWeight = 0.0
    for ((item, weight) <- weightedSeq) {
      currentWeight += weight
      if (currentWeight >= r) return item
    }

    throw new RuntimeException("Unable to choose a sample from given seq")
  }
}
