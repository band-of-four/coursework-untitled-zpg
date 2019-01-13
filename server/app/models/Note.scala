package models

import db.DbCtx

case class Note(text: String, textGender: Student.Gender, stage: Student.Stage,
                lessonId: Option[Long], clubId: Option[Long], creatureId: Option[Long], id: Long = -1)

class NoteDao(db: DbCtx) {
  import db._

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
