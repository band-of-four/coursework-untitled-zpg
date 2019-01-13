package models

import db.{DbCtx, PgEnum}
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

case class Spell(name: String, kind: Spell.Kind, power: Int, level: Int, id: Long = -1)

case class SpellsStudent(spellId: Long, studentId: Long)

object Spell {
  sealed trait Kind extends EnumEntry
  case object Kind extends Enum[Kind] with PgEnum[Kind] with PlayJsonEnum[Kind] {
    case object Attack extends Kind
    case object Defence extends Kind
    case object Luck extends Kind

    val values = findValues
  }
}

class SpellDao(val db: DbCtx) {
  import db._

  def createBaseSpells(studentId: Long): Unit =
    run(
      liftQuery(
        List(SpellsStudent(1, studentId), SpellsStudent(2, studentId), SpellsStudent(3, studentId))
      ).foreach(
        query[SpellsStudent].insert(_)
      )
    )

  def findLearned(studentId: Long): Seq[Spell] =
    run(
      query[Spell]
        .join(query[SpellsStudent])
        .on((sp, spst) => sp.id == spst.spellId && spst.studentId == lift(studentId))
        .map(_._1)
    )

  def findRandom(minLevel: Int, maxLevel: Int): Spell =
    run(
      query[Spell]
        .filter(s => s.level <= lift(maxLevel) && s.level >= lift(minLevel))
        .randomSort
        .take(1)
    ).head
}
