package models

import db.DbCtx

case class Note(text: String, textGender: Student.Gender, stage: Student.Stage,
                lessonId: Option[Long], clubId: Option[Long], creatureId: Option[Long], id: Long = -1)

case class NotePreloaded(text: String, stage: Student.Stage,
                         lesson: Option[String], club: Option[String], creature: Option[String])

class NoteDao(db: DbCtx) {
  import db._

  def load(noteId: Long): NotePreloaded =
    run(
      query[Note]
        .filter(_.id == lift(noteId))
        .leftJoin(query[Lesson]).on {
          case (n, l) => n.lessonId.exists(_ == l.id)
        }
        .leftJoin(query[StudentClub]).on {
          case ((n, l), cl) => n.clubId.exists(_ == cl.id)
        }
        .leftJoin(query[Creature]).on {
          case (((n, l), cl), cr) => n.creatureId.exists(_ == cr.id)
        }
        .map {
          case (((n, l), cl), cr)  => NotePreloaded(
            n.text, n.stage, l.map(_.name), cl.map(_.name), cr.map(_.name))
        }
    ).head

  def findIdForLesson(gender: Student.Gender, lessonId: Long): Long =
    run(findRandom.filter(n => n.textGender == lift(gender) && n.lessonId.exists(_ == lift(lessonId))).map(_.id)).head

  def findIdForCurrentStage(student: StudentForUpdate): Long =
    run(findRandom.filter(n => n.stage == lift(student.stage) && n.textGender == lift(student.gender)).map(_.id)).head

  def findIdForFight(student: StudentForUpdate, creatureId: Long): Long =
    run(
      findRandom
        .filter(n => n.stage == lift(student.stage) &&
          n.textGender == lift(student.gender) &&
          n.creatureId.exists(_ == lift(creatureId)))
        .map(_.id)
    ).head

  @inline private def findRandom =
    quote(query[Note].randomSort.take(1))
}
