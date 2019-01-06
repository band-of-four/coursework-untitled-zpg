package models

import db.DbCtx

case class Room(number: Long, level: Int, clubId: Option[Long], lessonId: Option[Long])

case class RoomPreloaded(number: Long, level: Int, club: Option[StudentClub], lesson: Option[Lesson])

object RoomPreloaded {
  def apply(room: Room, club: Option[StudentClub], lesson: Option[Lesson]) =
    new RoomPreloaded(room.number, room.level, club, lesson)
}

class RoomDao(val db: DbCtx) {
  import db._

  def preloadInRadius(aroundRoom: Long, radius: Long): Seq[RoomPreloaded] = {
    run(
      query[Room]
        .filter(r => r.number < lift(aroundRoom + radius) && r.number > lift(aroundRoom - radius))
        .leftJoin(query[StudentClub]).on((r, c) => r.clubId.exists(_ == c.id))
        .leftJoin(query[Lesson]).on((rc, l) => rc._1.lessonId.exists(_ == l.id))
    ).map {
      case ((r, c), l) => RoomPreloaded(r, c, l)
    }
  }
}
