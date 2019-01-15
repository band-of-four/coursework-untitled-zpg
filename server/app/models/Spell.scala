package models

import db.{DbCtx, PgEnum}
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import play.api.libs.json.Json

case class Spell(name: String, kind: Spell.Kind, power: Int, level: Int, id: Long = -1)

case class SpellsStudent(spellId: Long, studentId: Long)

case class SpellPreloaded(name: String, kind: Spell.Kind, power: Int)

object Spell {
  sealed trait Kind extends EnumEntry
  case object Kind extends Enum[Kind] with PgEnum[Kind] with PlayJsonEnum[Kind] {
    case object Attack extends Kind
    case object Defence extends Kind
    case object Luck extends Kind

    val values = findValues
  }
}

object SpellPreloaded {
  implicit val spellPreloadedWrites = Json.writes[SpellPreloaded]
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

  def load(studentId: Long): Seq[SpellPreloaded] =
    run(
      query[Spell]
        .join(query[SpellsStudent]).on {
          case (sp, spst) => sp.id == spst.spellId && spst.studentId == lift(studentId)
        }
        .map {
          case (s, spst) => SpellPreloaded(s.name, s.kind, s.power)
        }
    )

  def findRandomIdToLearn(studentId: Long, level: Int): Long =
    run(
      query[Spell]
        .filter(s => !query[SpellsStudent].filter(_.studentId == lift(studentId)).map(_.spellId).contains(s.id))
        .filter(_.level == lift(level))
        .map(_.id)
        .takeRandom
    ).head
}
