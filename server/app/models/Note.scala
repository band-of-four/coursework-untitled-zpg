package models

import db.DbCtx

case class Note(text: String, stage: Student.Stage,
                lessonId: Option[Long], clubId: Option[Long], creatureId: Option[Long], id: Long = -1)

class NoteDao(db: DbCtx) {
  import db._

  def findIdForFightWith(creatureId: Long): Long =
    run(
      query[Note]
        .filter(_.stage == lift(Student.Stage.Fight: Student.Stage))
        .filter(_.creatureId.exists(_ == lift(creatureId)))
        .map(_.id)
        .randomSort
        .take(1)
    ).head
}
