package models
import db.DbCtx
import models.{Room, Student}

case class Club(name: String, id: Long = -1)

class ClubDao(db: DbCtx) {
  import db._

  def findNamesForStudentId(studentId: Long): Seq[String] =
    run(
      query[Student]
        .filter(_.id == lift(studentId))
        .join(query[Room]).on {
          case (s, r) => s.level == r.level
        }
        .join(query[Club]).on {
          case ((s, r), c) => r.clubId.exists(_ == c.id)
        }
        .map {
          case ((s, r), c) => c.name
        }
      )

  def findIdByName(clubName: String): Long =
    run(query[Club].filter(_.name == lift(clubName)).map(_.id)).head
}
