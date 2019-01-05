package models

import java.time.LocalDateTime

import db.DbCtx

case class Student(stage: String, nextStageTime: LocalDateTime,
                   academicYear: Int, currentRoom: Long, id: Long = -1)

object Student {
  val StageTravel = "travel"
  val StageFight = "fight"
  val StageClub = "club"
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
}
