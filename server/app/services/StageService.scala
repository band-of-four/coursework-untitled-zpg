package services

import models._
import play.api.libs.json.Json
import java.time.{Duration, LocalDateTime, ZoneId}

import game.GameProgressionResource
import services.GameProgressionService.CompletedStage
import services.NoteService.FormattedNote
import services.StageService.StageUpdate

object StageService {
  case class StageUpdate(level: Int, hp: Int, note: FormattedNote, stageDuration: Long, stageElapsed: Long,
                         updated: Seq[GameProgressionResource] = Nil)

  implicit val updateWrites = Json.writes[StageUpdate]
}

class StageService(studentDao: StudentDao, noteDao: NoteDao, diaryDao: StudentDiaryDao, noteService: NoteService) {
  def getStage(userId: Long): StageUpdate = {
    val student = studentDao.findForUser(userId).get
    val stageNote = noteService.loadStageNote(student)

    val startTime = student.stageStartTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
    val endTime = student.nextStageTime.atZone(ZoneId.systemDefault()).toInstant.toEpochMilli
    val duration = endTime - startTime
    val elapsed = System.currentTimeMillis() - startTime

    StageUpdate(student.level, student.hp, stageNote, duration, elapsed)
  }

  def findPendingUpdates(count: Int): Seq[StudentForUpdate] =
    studentDao.findPendingStageUpdate(count)

  def transactionalUpdateWithResult(userId: Long)(block: => CompletedStage): StageUpdate = {
    val completedStage = studentDao.doTransaction(block)
    getStage(userId).copy(updated = completedStage.updates)
  }

  def writeFightResultToDiary(student: StudentForUpdate, opponentId: Long, outcome: Student.Stage): Unit = {
    require(outcome == Student.Stage.FightWon || outcome == Student.Stage.FightLost)

    writeStageNoteToDiary(student)
    val fightEndNote = noteDao.findIdForFight(student.copy(stage = outcome), opponentId)
    diaryDao.createEntry(StudentDiaryEntry(student.id, fightEndNote, LocalDateTime.now()))
  }

  def writeStageNoteToDiary(student: StudentForUpdate): Unit =
    diaryDao.writeStageNote(student.id, LocalDateTime.now())

  def setFightNote(student: StudentForUpdate, opponentId: Long, duration: Duration): Unit = {
    val fightNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.Fight), opponentId)
    studentDao.updateStage(student, fightNote, duration)
  }

  def setLessonNote(student: StudentForUpdate, lessonId: Long, duration: Duration): Unit = {
    val lessonNote = noteDao.findIdForLesson(student.gender, lessonId)
    studentDao.updateStage(student, lessonNote, duration)
  }

  def setClubNote(student: StudentForUpdate, clubId: Long, duration: Duration): Unit = {
    val clubNote = noteDao.findIdForClub(student.gender, clubId)
    studentDao.updateStage(student, clubNote, duration)
  }

  def setGenericStageNote(student: StudentForUpdate, stage: Student.Stage, duration: Duration): Unit = {
    require(stage == Student.Stage.Travel || stage == Student.Stage.Library || stage == Student.Stage.Infirmary)

    val genericNote = noteDao.findIdForCurrentStage(student.copy(stage = stage))
    studentDao.updateStage(student, genericNote, duration)
  }
}
