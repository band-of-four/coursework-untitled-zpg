package models

import java.time.LocalDateTime

import play.api.libs.json.Json
import db.{DbCtx, Pagination}

case class StudentDiaryEntry(studentId: Long, noteId: Long, date: LocalDateTime)

case class StudentDiaryNote(text: String, date: LocalDateTime, stage: Student.Stage,
                            lesson: Option[String], club: Option[String], creature: Option[String])

object StudentDiaryNote { implicit val jsonWrites = Json.format[StudentDiaryNote] }

class StudentDiaryDao(db: DbCtx) {
  import db._

  private val schema = quote(querySchema[StudentDiaryEntry]("student_diary_entries"))

  def load(studentId: Long)(implicit pagination: Pagination): Seq[StudentDiaryNote] =
    run(
      schema
        .filter(_.studentId == lift(studentId))
        .sortBy(_.date)(Ord.desc)
        .join(query[Note]).on {
          case (sde, n) => sde.noteId == n.id
        }
        .leftJoin(query[Lesson]).on {
          case ((sde, n), l) => n.lessonId.exists(_ == l.id)
        }
        .leftJoin(query[Club]).on {
          case (((sde, n), l), cl) => n.clubId.exists(_ == cl.id)
        }
        .leftJoin(query[Creature]).on {
          case ((((sde, n), l), cl), cr) => n.creatureId.exists(_ == cr.id)
        }
        .map {
          case ((((sde, n), l), cl), cr) =>
            StudentDiaryNote(n.text, sde.date, n.stage, l.map(_.name), cl.map(_.name), cr.map(_.name))
        }
        .paginate
    )

  def createEntry(entry: StudentDiaryEntry): Unit =
    run(schema.insert(lift(entry)))
}
