package game

import java.time.Duration

import models._
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

  sealed trait TurnOutcome
  case class FightContinues(student: StudentForUpdate, opponent: OpponentCreature) extends TurnOutcome
  case class StudentWon(student: StudentForUpdate, opponent: OpponentCreature) extends TurnOutcome
  case class StudentLost(student: StudentForUpdate, opponent: OpponentCreature) extends TurnOutcome

  def computeTurn(student: StudentForUpdate, creature: OpponentCreature, spells: Seq[Spell]): TurnOutcome = {
    val attackSpell: Int = spells.find(_.kind == AttackSpell).get.power
    val luckSpell: Int = spells.find(_.kind == LuckSpell).get.power
    val defenceSpell: Int = spells.find(_.kind == DefenceSpell).get.power

    val studentAttack = (attackSpell +
      (student.level * StudentAttackLevelWeight) +
      (creature.studentsSkill * StudentAttackSkillWeight) +
      (luckSpell * RandomDouble(LuckSpellRange))).toInt

    val creatureAttack = ((creature.level * CreatureAttackLevelWeight) +
      (creature.studentsSkill * CreatureAttackSkillWeight) -
      defenceSpell).toInt

    val newCreatureHp = creature.hp - studentAttack
    val newStudentHp = student.hp - creatureAttack

    if (newCreatureHp <= 0)
      StudentWon(student, creature)
    else if (newStudentHp <= 0)
      StudentLost(student, creature)
    else
      FightContinues(student.copy(hp = newStudentHp), creature.copy(hp = newCreatureHp))
  }
}
