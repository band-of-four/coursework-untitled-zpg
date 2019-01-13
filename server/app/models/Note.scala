package models

import db.DbCtx

case class Note(text: String, textGender: Student.Gender, stage: Student.Stage,
                lessonId: Option[Long], clubId: Option[Long], creatureId: Option[Long], id: Long = -1)

class NoteDao(db: DbCtx) {
  import db._

  def findIdForFight(student: StudentForUpdate, creatureId: Long): Long =
    run(
      query[Note]
        .filter(_.stage == lift(student.stage))
        .filter(_.textGender == lift(student.gender))
        .filter(_.creatureId.exists(_ == lift(creatureId)))
        .map(_.id)
        .randomSort
        .take(1)
    ).head
}
