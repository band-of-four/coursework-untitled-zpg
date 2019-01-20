package game

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}

sealed trait GameProgressionResource extends EnumEntry

case object GameProgressionResource extends Enum[GameProgressionResource]
                                    with PlayJsonEnum[GameProgressionResource] {
  case object Diary extends GameProgressionResource
  case object Spells extends GameProgressionResource
  case object LessonAttendance extends GameProgressionResource
  case object Relationships extends GameProgressionResource
  case object CreatureHandlingSkills extends GameProgressionResource

  val values = findValues
}
