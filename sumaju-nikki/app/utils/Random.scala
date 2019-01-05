package utils

import java.util.concurrent.ThreadLocalRandom

object RandomEvent {
  def apply(odd: Double) = ThreadLocalRandom.current().nextDouble() < odd
}

object WeightedSample {
  def apply[A](elements: Seq[A])(weightMap: A => Double): A = {
    val weights = elements.map(weightMap)

    val r = ThreadLocalRandom.current().nextDouble(0, weights.sum)

    var currentWeight = 0.0
    for ((item, weight) <- elements.zip(weights)) {
      currentWeight += weight
      if (currentWeight >= r) return item
    }

    throw new RuntimeException("Unable to choose a sample from given seq")
  }
}
