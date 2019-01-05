package models

import db.DbCtx

case class Room(number: Long, clubId: Option[Long], lessonId: Option[Long])
case class RoomPreloaded(number: Long, club: Option[StudentClub], lesson: Option[Lesson])

class RoomDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Room]("rooms"))

  def preloadInRadius(aroundRoom: Long, radius: Long): Seq[RoomPreloaded] = {
    run(
      schema
        .filter(r => r.number < lift(aroundRoom + radius) && r.number > lift(aroundRoom - radius))
        .leftJoin(query[StudentClub]).on((r, c) => r.clubId.exists(_ == c.id))
        .leftJoin(query[Lesson]).on((rc, l) => rc._1.lessonId.exists(_ == l.id))
    ).map {
      case ((r, c), l) => RoomPreloaded(r.number, c, l)
    }
  }
}
