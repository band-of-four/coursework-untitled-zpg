package game

import java.time.Duration

import models.{Creature, OpponentCreature, Spell, Student}
import models.Spell.{AttackSpell, DefenceSpell, LuckSpell}
import utils.RandomDouble

object Fight {
  val FightChance = 0.75
  val FightTurnDuration = Duration.ofSeconds(25)

  val LuckSpellRange = 0.3
  val StudentAttackLevelWeight = 10.0
  val StudentAttackSkillWeight = 2.0
  val CreatureAttackLevelWeight = 8.0
  val CreatureAttackSkillWeight = -2.0
  val StudentPetWeight = 1.0

  sealed trait TurnResult
  case class FightContinues(newStudentHp: Int, newCreatureHp: Int) extends TurnResult
  case object StudentWon extends TurnResult
  case object StudentLost extends TurnResult

  def computeTurn(student: Student, creature: OpponentCreature, spells: Seq[Spell]): TurnResult = {
    val attackSpell: Int = spells.find(_.kind == AttackSpell).get.power
    val luckSpell: Int = spells.find(_.kind == LuckSpell).get.power
    val defenceSpell: Int = spells.find(_.kind == DefenceSpell).get.power
    val pet: Creature = ??? // TODO find the way to store and get pets

    val studentAttack = (attackSpell +
      (student.level * StudentAttackLevelWeight) +
      (creature.studentsSkill * StudentAttackSkillWeight) +
      (luckSpell * RandomDouble(LuckSpellRange)) +
      (pet.power * StudentPetWeight)).toInt

    val creatureAttack = ((creature.level * CreatureAttackLevelWeight) +
      (creature.studentsSkill * CreatureAttackSkillWeight) -
      defenceSpell).toInt

    val newCreatureHp = creature.hp - studentAttack
    val newStudentHp = student.hp - creatureAttack

    if (newCreatureHp <= 0)
      StudentWon
    else if (newStudentHp <= 0)
      StudentLost
    else
      FightContinues(newStudentHp, newCreatureHp)
  }
}
