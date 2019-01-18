package services

import models._
import play.api.libs.json.Json
import services.StageService.StageUpdate
import java.time.{Duration, LocalDateTime, ZoneId}

import game.Fight.{FightContinues, StudentLost, StudentWon, TurnOutcome}
import services.GameProgressionService.{CompletedGenericStage, CompletedLesson, CompletedLibrary, StageCompletion}
import services.NoteService.FormattedNote

object StageService {
  case class StageUpdate(level: Int, hp: Int, note: FormattedNote, stageDuration: Long, stageElapsed: Long,
                         attendance: Option[Seq[LessonAttendancePreloaded]] = None,
                         spells: Option[Seq[SpellPreloaded]] = None)

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

  def transactionalUpdateWithResult(userId: Long)(block: => StageCompletion): StageUpdate = {
    val updateResult = studentDao.doTransaction(block)
    val genericUpdate = getStage(userId)

    updateResult match {
      case CompletedLesson(attendance) =>
        genericUpdate.copy(attendance = Some(attendance))
      case CompletedLibrary(spells) =>
        genericUpdate.copy(spells = Some(spells))
      case CompletedGenericStage =>
        genericUpdate
    }
  }

  def commitFightStage(fightTurnOutcome: TurnOutcome, turnDuration: Duration): Unit = fightTurnOutcome match {
    case FightContinues(student, opponent) =>
      commitFight(student, opponent.id, turnDuration)
    case StudentWon(student, opponent) =>
      val randomFightNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.Fight), opponent.id)
      val fightEndNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.FightWon), opponent.id)
      diaryDao.createEntry(StudentDiaryEntry(student.id, randomFightNote, LocalDateTime.now().minus(turnDuration)))
      diaryDao.createEntry(StudentDiaryEntry(student.id, fightEndNote, LocalDateTime.now()))
    case StudentLost(student, opponent) =>
      val randomFightNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.Fight), opponent.id)
      val fightEndNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.FightLost), opponent.id)
      diaryDao.createEntry(StudentDiaryEntry(student.id, randomFightNote, LocalDateTime.now().minus(turnDuration)))
      diaryDao.createEntry(StudentDiaryEntry(student.id, fightEndNote, LocalDateTime.now()))
  }

  def commitFight(student: StudentForUpdate, opponentId: Long, duration: Duration): Unit = {
    val fightNote = noteDao.findIdForFight(student.copy(stage = Student.Stage.Fight), opponentId)
    studentDao.updateStage(student, fightNote, duration)
  }

  def commitLessonStage(student: StudentForUpdate, lessonId: Long, duration: Duration): Unit = {
    val lessonNote = noteDao.findIdForLesson(student.gender, lessonId)
    studentDao.updateStage(student, lessonNote, duration)
  }

  def commitClubStage(student: StudentForUpdate, clubId: Long, duration: Duration): Unit = {
    val clubNote = noteDao.findIdForClub(student.gender, clubId)
    studentDao.updateStage(student, clubNote, duration)
  }

  def commitTravelStage(student: StudentForUpdate, duration: Duration): Unit = {
    val travelNote = noteDao.findIdForCurrentStage(student.copy(stage = Student.Stage.Travel))
    studentDao.updateStage(student, travelNote, duration)
  }

  def commitLibraryStage(student: StudentForUpdate, duration: Duration): Unit = {
    val libraryNote = noteDao.findIdForCurrentStage(student.copy(stage = Student.Stage.Library))
    studentDao.updateStage(student, libraryNote, duration)
  }

  def commitInfirmaryStage(student: StudentForUpdate, duration: Duration): Unit = {
    val infirmaryNote = noteDao.findIdForCurrentStage(student.copy(stage = Student.Stage.Infirmary))
    studentDao.updateStage(student, infirmaryNote, duration)
  }
}
