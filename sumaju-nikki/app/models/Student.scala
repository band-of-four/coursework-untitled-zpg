package models

import java.time.{Duration, LocalDateTime}

import db.DbCtx

case class Student(stage: String, nextStageTime: LocalDateTime,
                   academicYear: Int, currentRoom: Long, hp: Int, id: Long = -1)

object Student {
  val StageTravel = "travel"
  val StageFight = "fight"
  val StageStudy = "study"
}

class StudentDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Student]("students"))

  def findPendingTurnUpdates(count: Int): Seq[Student] =
    run(
      schema
        .filter(_.nextStageTime <= lift(LocalDateTime.now()))
        .sortBy(_.nextStageTime)(Ord.asc)
        .take(lift(count))
        .forUpdate
    )

  def updateStage(studentId: Long, newRoom: Long, stage: String, stageDuration: Duration): Unit =
    run(
      schema
        .filter(_.id == lift(studentId))
        .update(_.stage -> lift(stage),
          _.currentRoom -> lift(newRoom),
          _.nextStageTime -> lift(LocalDateTime.now().plus(stageDuration)))
    )
}
