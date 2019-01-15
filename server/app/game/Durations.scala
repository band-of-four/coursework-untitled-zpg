package game

import java.time.Duration

import models.StudentForUpdate

object Durations {
  val Library = Duration.ofSeconds(15)
  val FightTurn = Duration.ofSeconds(15)
  val Study = Duration.ofSeconds(15)
  val Travel = Duration.ofSeconds(15)
  val Club = Duration.ofSeconds(15)

  object Infirmary {
    def apply(student: StudentForUpdate): Duration =
      Duration.ofSeconds((student.level + 1) * 15)
  }
}
