package game

import java.time.Duration

import models.StudentForUpdate

object Heal {
  def duration(student: StudentForUpdate): Duration =
    Duration.ofMinutes(student.level * 5)
}
