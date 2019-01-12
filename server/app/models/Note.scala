package models

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import db.PgEnum

case class Note(text: String, kind: Note.Kind,
                lessonId: Option[Long], clubId: Option[Long], creatureId: Option[Long], id: Long = -1)

object Note {
  sealed trait Kind extends EnumEntry
  case object Kind extends Enum[Kind] with PgEnum[Kind] with PlayJsonEnum[Kind] {
    case object Lesson extends Kind
    case object Club extends Kind
    case object Fight extends Kind
    case object Infirmary extends Kind

    val values = findValues
  }
}
