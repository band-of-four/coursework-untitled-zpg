package models

import java.time.{Duration, LocalDateTime}

import db.DbCtx
import models.Student.StageHeal

case class Student(stage: String, nextStageTime: LocalDateTime,
                   level: Int, currentRoom: Long, hp: Int, id: Long = -1)

object Student {
  val StageTravel = "travel"
  val StageFight = "fight"
  val StageStudy = "study"
  val StageHeal = "heal"
}

class StudentDao(val db: DbCtx) {
  import db._

  def findForUser(userId: Long): Option[Student] =
    run(query[Student].filter(_.id == lift(userId))).headOption

  def findPendingTurnUpdates(count: Int): Seq[Student] =
    run(
      query[Student]
        .filter(_.nextStageTime <= lift(LocalDateTime.now()))
        .sortBy(_.nextStageTime)(Ord.asc)
        .take(lift(count))
        .forUpdate
    )

  def updateInFight(student: Student, newHp: Int, turnDuration: Duration): Unit =
    run(
      query[Student]
        .filter(_.id == lift(student.id))
        .update(_.hp -> lift(newHp),
          _.nextStageTime -> lift(LocalDateTime.now().plus(turnDuration)))
    )

  def updateAfterLostFight(student: Student, healDuration: Duration, infirmaryRoom: Long): Unit =
    run(
      query[Student]
        .update(_.stage -> lift(StageHeal),
          _.nextStageTime -> lift(LocalDateTime.now().plus(healDuration)),
          _.currentRoom -> lift(infirmaryRoom))
    )

  def updateStage(studentId: Long, newRoom: Long, stage: String, stageDuration: Duration): Unit =
    run(
      query[Student]
        .filter(_.id == lift(studentId))
        .update(_.stage -> lift(stage),
          _.currentRoom -> lift(newRoom),
          _.nextStageTime -> lift(LocalDateTime.now().plus(stageDuration)))
    )
}
