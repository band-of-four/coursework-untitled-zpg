package models

import db.DbCtx

object Room {
  object Kind extends Enumeration {
    type Kind = Value
    val Classroom, Clubroom, Library, Infirmary = Value
  }
}

case class Room(number: Long, level: Int, kind: Room.Kind.Value, clubId: Option[Long], lessonId: Option[Long])

case class RoomPreloaded(number: Long, level: Int, kind: Room.Kind.Value, club: Option[StudentClub], lesson: Option[Lesson])

class RoomDao(val db: DbCtx) {
  import db._

  implicit val decoderRoomKind: Decoder[Room.Kind.Value] = decoder((index, row) =>
    Room.Kind.withName(row.getObject(index).toString split "_" map (p => p.head.toUpper + p.tail) mkString))

  implicit val encoderRoomKind: Encoder[Room.Kind.Value] =
    encoder(java.sql.Types.VARCHAR, (index, value, row) =>
      row.setObject(index, value, java.sql.Types.OTHER))

  def preloadInRadius(aroundRoom: Long, radius: Long): Seq[RoomPreloaded] = {
    run(
      query[Room]
        .filter(r =>
          r.number < lift(aroundRoom + radius) && r.number > lift(aroundRoom - radius)
        )
        .leftJoin(query[StudentClub]).on {
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
