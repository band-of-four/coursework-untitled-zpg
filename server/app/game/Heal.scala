package game

import java.time.Duration

import models.Student

object Heal {
  def duration(student: Student): Duration =
    Duration.ofMinutes(student.level * 5)
}
