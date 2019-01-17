package game

import models._
import utils.RandomDouble

object Fight {
  val FightChance = 0.75

  case class BaseCreatureStats(totalHp: Int, power: Int)
  object BaseCreatureStats {
    def apply(level: Int): BaseCreatureStats =
      BaseCreatureStats(totalHp = 20 * (level + 1), power = 5 * (level + 1))
  }

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

  def computeTurn(student: StudentForUpdate, creature: OpponentCreature, spells: Seq[SpellPreloaded]): TurnOutcome = {
    val attackSpell = spells.find(_.kind == Spell.Kind.Attack).get.power
    val defenceSpell = spells.find(_.kind == Spell.Kind.Defence).get.power
    val luckSpell = spells.find(_.kind == Spell.Kind.Luck).get.power

    val studentAttack = (attackSpell +
      ((student.level + 1) * StudentAttackLevelWeight) +
      (creature.studentsSkill.getOrElse(0) * StudentAttackSkillWeight) +
      (luckSpell * RandomDouble(LuckSpellRange))).toInt

    val creatureAttack = (((creature.level + 1) * CreatureAttackLevelWeight) +
      (creature.studentsSkill.getOrElse(0) * CreatureAttackSkillWeight) -
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
