package models

import java.time.{Duration, LocalDateTime}

import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import db.{DbCtx, PgEnum}

object Student {
  sealed trait Gender extends EnumEntry
  case object Gender extends Enum[Gender] with PgEnum[Gender] with PlayJsonEnum[Gender] {
    case object Female extends Gender
    case object Male extends Gender

    val values = findValues
  }

  sealed trait Stage extends EnumEntry
  case object Stage extends Enum[Stage] with PgEnum[Stage] with PlayJsonEnum[Stage] {
    case object Travel extends Stage
    case object Fight extends Stage
    case object Study extends Stage
    case object Heal extends Stage

    val values = findValues
  }
}

case class Student(name: String, gender: Student.Gender, level: Int, hp: Int, currentRoom: Long,
                   stage: Student.Stage, stageStartTime: LocalDateTime, nextStageTime: LocalDateTime,
                   id: Long = -1)

class StudentDao(val db: DbCtx) {
  import db._

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
        .update(_.stage -> lift(Student.Stage.Heal: Student.Stage),
          _.stageStartTime -> lift(LocalDateTime.now()),
          _.nextStageTime -> lift(LocalDateTime.now().plus(healDuration)),
          _.currentRoom -> lift(infirmaryRoom))
    )

  def updateStage(studentId: Long, newRoom: Long, stage: Student.Stage, stageDuration: Duration): Unit =
    run(
      query[Student]
        .filter(_.id == lift(studentId))
        .update(_.stage -> lift(stage),
          _.currentRoom -> lift(newRoom),
          _.stageStartTime -> lift(LocalDateTime.now()),
          _.nextStageTime -> lift(LocalDateTime.now().plus(stageDuration)))
    )
}
