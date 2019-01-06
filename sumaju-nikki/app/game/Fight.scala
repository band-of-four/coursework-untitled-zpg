package game

import java.time.Duration

import models.Student
import utils.RandomDouble

object Fight {
  val FightChance = 0.75
  val FightTurnDuration = Duration.ofSeconds(25)
  val StudentAttackWeights = List(1, 1, 1, 1)
  val LuckSpellRange = 0.3

  val StudentAttackLevelWeight = 10.0
  val StudentAttackCreatureModWeight = 2.0

  def studentAttack(student: Student, creatureHandlingMod: Int): Int = {
    val attackSpell: Int = ???
    val luckSpell: Int = ???

    val total = attackSpell +
      (student.academicYear * StudentAttackLevelWeight) +
      (creatureHandlingMod * StudentAttackCreatureModWeight) +
      (RandomDouble(LuckSpellRange) * luckSpell)

    total.toInt
  }
}
