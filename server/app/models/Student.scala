package models

import java.time.{Duration, LocalDateTime}

import db.DbCtx
import models.Student.{Gender, StageHeal}

object Student {
  object Gender extends Enumeration {
    type Gender = Value
    val Female, Male = Value
  }

  val StageTravel = "travel"
  val StageFight = "fight"
  val StageStudy = "study"
  val StageHeal = "heal"
}

case class Student(name: String, gender: Gender.Value, level: Int, hp: Int, currentRoom: Long,
                   stage: String, stageStartTime: LocalDateTime, nextStageTime: LocalDateTime,
                   id: Long = -1)

class StudentDao(val db: DbCtx) {
  import db._

  implicit val decoderGender: Decoder[Gender.Value] = decoder((index, row) =>
    Gender.withName(row.getObject(index).toString.capitalize))

  implicit val encoderGender: Encoder[Gender.Value] = encoder(java.sql.Types.VARCHAR, (index, value, row) =>
      row.setObject(index, value.toString.toLowerCase, java.sql.Types.OTHER))

  def create(student: Student): Student = {
    run(query[Student].insert(lift(student)))
    student
  }

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
          _.stageStartTime -> lift(LocalDateTime.now()),  
          _.nextStageTime -> lift(LocalDateTime.now().plus(turnDuration)))
    )

  def updateAfterLostFight(student: Student, healDuration: Duration, infirmaryRoom: Long): Unit =
    run(
      query[Student]
        .update(_.stage -> lift(StageHeal),
          _.stageStartTime -> lift(LocalDateTime.now()),
          _.nextStageTime -> lift(LocalDateTime.now().plus(healDuration)),
          _.currentRoom -> lift(infirmaryRoom))
    )

  def updateStage(studentId: Long, newRoom: Long, stage: String, stageDuration: Duration): Unit =
    run(
      query[Student]
        .filter(_.id == lift(studentId))
        .update(_.stage -> lift(stage),
          _.currentRoom -> lift(newRoom),
          _.stageStartTime -> lift(LocalDateTime.now()),
          _.nextStageTime -> lift(LocalDateTime.now().plus(stageDuration)))
    )
}
