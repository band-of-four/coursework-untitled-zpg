package services

import models._
import play.api.libs.json.Json
import services.StageService.CurrentStage
import java.time.{LocalDateTime, ZoneId}

import game.Fight._
import game.Heal
import game.Study.StudyDuration
import game.Travel.TravelDuration

object StageService {
  case class CurrentStage(kind: Student.Stage, durationMs: Long, elapsedMs: Long)

  implicit val stageWrites = Json.writes[CurrentStage]
}

class StageService(studentDao: StudentDao, roomDao: RoomDao, noteDao: NoteDao, diaryDao: StudentDiaryDao) {
  def getCurrentStage(userId: Long): CurrentStage = {
    val student = studentDao.findForUser(userId).get

    val startTime = student.stageStartTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
    val endTime = student.nextStageTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
    val duration = endTime - startTime

    CurrentStage(
      ???,
      durationMs = endTime - startTime,
      elapsedMs = System.currentTimeMillis() - startTime
    )
  }

  def commitFightStage(fightTurnOutcome: TurnOutcome): Unit = fightTurnOutcome match {
    case FightContinues(student, opponent) =>
      commitFight(student, opponent.id)
    case StudentWon(student, opponent) =>
      val randomFightNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.Fight), opponent.id)
      val fightEndNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.FightWon), opponent.id)
      diaryDao.createEntry(StudentDiaryEntry(student.id, randomFightNote, LocalDateTime.now().minus(FightTurnDuration)))
      diaryDao.createEntry(StudentDiaryEntry(student.id, fightEndNote, LocalDateTime.now()))
      commitTravelStage(student)
    case StudentLost(student, opponent) =>
      val randomFightNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.Fight), opponent.id)
      val fightEndNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.FightLost), opponent.id)
      diaryDao.createEntry(StudentDiaryEntry(student.id, randomFightNote, LocalDateTime.now().minus(FightTurnDuration)))
      diaryDao.createEntry(StudentDiaryEntry(student.id, fightEndNote, LocalDateTime.now()))
      commitInfirmaryStage(student)
  }

  def commitFight(student: StudentForUpdate, opponentId: Long): Unit = {
    val fightNote = noteDao.findIdForFight(student, opponentId)
    studentDao.updateStage(student, fightNote, FightTurnDuration)
  }

  def commitTravelStage(student: StudentForUpdate): Unit = {
    val travelNote = noteDao.findIdForCurrentStage(student.copy(stage = Student.Stage.Travel))
    studentDao.updateStage(student, travelNote, TravelDuration)
  }

  def commitRoomEntry(student: StudentForUpdate): Unit = {
    val room = roomDao.findByNumber(student.currentRoom)
    room.kind match {
      case Room.Kind.Classroom =>
        val lessonNote = noteDao.findIdForCurrentStage(student.copy(stage = Student.Stage.Lesson))
        studentDao.updateStage(student, lessonNote, StudyDuration)
      case _ =>
        ???
    }
  }

  def commitInfirmaryStage(student: StudentForUpdate): Unit = {
    val infirmary = roomDao.findClosest(Room.Kind.Infirmary, student.currentRoom)
    val infirmaryNote = noteDao.findIdForCurrentStage(student.copy(stage = Student.Stage.Infirmary))
    studentDao.updateStage(student, infirmaryNote, Heal.duration(student))
  }
}
