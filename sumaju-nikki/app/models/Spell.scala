package models

import db.DbCtx

case class Spell(name: String, kind: String, power: Int, academicYear: Int, id: Long = -1)

case class SpellsStudent(spellId: Long, studentId: Long)

object Spell {
  val AttackSpell = "attack"
  val LuckSpell = "luck"
}

class SpellDao(val db: DbCtx) {
  import db._

  def findLearned(student: Student): Seq[Spell] =
    run(
      query[Spell]
        .join(query[SpellsStudent])
        .on((sp, spst) => sp.id == spst.spellId && spst.studentId == lift(student.id))
        .map(_._1)
    )

  def findRandom(minLevel: Int, maxLevel: Int): Spell =
    run(
      query[Spell]
        .filter(s => s.academicYear <= lift(maxLevel) && s.academicYear >= lift(minLevel))
        .randomSort
        .take(1)
    ).head
}
