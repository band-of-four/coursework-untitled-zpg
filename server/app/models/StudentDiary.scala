package models

import java.time.LocalDateTime

import db.{DbCtx, Pagination}

case class StudentDiaryEntry(studentId: Long, noteId: Long, date: LocalDateTime)

case class StudentDiaryNote(id: Long, text: String, date: LocalDateTime, stage: Student.Stage,
                            lesson: Option[String], club: Option[String], creature: Option[String],
                            heartCount: Long, isHearted: Boolean)

class StudentDiaryDao(db: DbCtx) {
  import db._

  private val schema = quote(querySchema[StudentDiaryEntry]("student_diary_entries"))

  def load(studentId: Long, pagination: Pagination): Seq[StudentDiaryNote] =
    run(
      schema
        .filter(_.studentId == lift(studentId))
        .paginate(lift(pagination))
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
        .leftJoin(query[NoteHeartsUser]).on {
          case (((((sde, n), l), cl), cr), heart) => heart.noteId == n.id && heart.userId == lift(studentId)
        }
        .map {
          case (((((sde, n), l), cl), cr), heart) => StudentDiaryNote(
            n.id, n.text, sde.date, n.stage, l.map(_.name), cl.map(_.name), cr.map(_.name), n.heartCount, heart.nonEmpty)
        }
        .sortBy(_.date)(Ord.desc) // repeated sortBy is intentional, due to joins producing a subquery in quill (check the generated sql)
    )

  def createEntry(entry: StudentDiaryEntry): Unit =
    run(schema.insert(lift(entry)))

  def writeStageNote(studentId: Long, date: LocalDateTime): Unit =
    run(infix"""SELECT student_diary_entry_write_stage_note(${lift(studentId)}, ${lift(date)})""".as[Query[String]])
}
