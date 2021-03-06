package models

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import db.{DbCtx, PgEnum}

object Room {
  sealed trait Kind extends EnumEntry
  case object Kind extends Enum[Kind] with PgEnum[Kind] with PlayJsonEnum[Kind] {
    case object Classroom extends Kind
    case object Clubroom extends Kind
    case object Library extends Kind
    case object Infirmary extends Kind

    val values = findValues
  }
}

case class Room(number: Long, level: Int, kind: Room.Kind, clubId: Option[Long], lessonId: Option[Long])

case class RoomPreloaded(number: Long, level: Int, kind: Room.Kind, club: Option[Club], lesson: Option[Lesson])

class RoomDao(val db: DbCtx) {
  import db._

  def findByNumber(number: Long): Room =
    run(query[Room].filter(_.number == lift(number))).head

  def findInfirmaryNumber(level: Int): Long =
    run(
      query[Room]
        .filter(r => r.level == lift(level) && r.kind == lift(Room.Kind.Infirmary: Room.Kind))
        .map(_.number)
        .take(1)
    ).head

  def preloadInRadius(aroundRoom: Long, radius: Long): Seq[RoomPreloaded] = {
    run(
      query[Room]
        .filter(_.number != lift(aroundRoom))
        .filter(r => r.number < lift(aroundRoom + radius) && r.number > lift(aroundRoom - radius))
        .leftJoin(query[Club]).on {
          case (r, c) => r.clubId.exists(_ == c.id)
        }
        .leftJoin(query[Lesson]).on {
          case ((r, c), l) => r.lessonId.exists(_ == l.id)
        }
    ).map {
      case ((r, c), l) => RoomPreloaded(r.number, r.level, r.kind, c, l)
    }
  }
}
