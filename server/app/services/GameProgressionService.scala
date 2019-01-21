package services

import models._
import models.Student.Stage
import game.{Durations, Fight, Stats, Travel, GameProgressionResource => Resource}
import game.Fight._
import game.Travel._
import services.GameProgressionService._
import services.StageService.StageUpdate
import utils.RandomEvent

object GameProgressionService {
  case class CompletedStage(updates: Seq[Resource] = Nil)
}

class GameProgressionService(stageService: StageService,
                             owlService: OwlService,
                             libraryService: LibraryService,
                             roomDao: RoomDao,
                             lessonDao: LessonDao,
                             creatureDao: CreatureDao,
                             spellDao: SpellDao,
                             relationshipDao: StudentRelationshipDao) {
  def pendingUpdates(count: Int): Seq[StudentForUpdate] =
    stageService.findPendingUpdates(count)

  def updateStage(student: StudentForUpdate): StageUpdate =
    stageService.transactionalUpdateWithResult(student.id) {
      owlService.useActiveOwlsForUpdate(student) { owls =>
        student.stage match {
          case Stage.Fight =>
            continueFight(student, owls)
          case Stage.Travel if RandomEvent(FightChance) =>
            startFight(student)
            CompletedStage()
          case Stage.Travel =>
            owlService.assignRandomToStudent(student.id)
            enterNextRoom(student)
            CompletedStage(updates = Seq(Resource.Owls))
          case Stage.Club =>
            relationshipDao.updateInClub(student.id)
            stageService.writeStageNoteToDiary(student)
            stageService.setGenericStageNote(student, Stage.Travel, Durations.Travel)
            CompletedStage(updates = Seq(Resource.Diary, Resource.Relationships))
          case Stage.Lesson =>
            lessonDao.updateAttendance(student.id)
            if (lessonDao.checkAttendance(student.id))
              owlService.giveLevelUp(student.id)
            stageService.writeStageNoteToDiary(student)
            stageService.setGenericStageNote(student, Stage.Travel, Durations.Travel)
            CompletedStage(updates = Seq(Resource.Diary, Resource.LessonAttendance))
          case Stage.Library =>
            libraryService.commitVisitEnd(student)
            stageService.setGenericStageNote(student, Stage.Travel, Durations.Travel)
            CompletedStage(updates = Seq(Resource.Spells))
          case Stage.Infirmary =>
            stageService.writeStageNoteToDiary(student)
            stageService.setGenericStageNote(student.copy(hp = Stats.MaxHpPerLevel(student.level)), Stage.Travel, Durations.Travel)
            CompletedStage(updates = Seq(Resource.Diary))
        }
      }
    }

  def startFight(student: StudentForUpdate): Unit = {
    val opponent = creatureDao.findNearRoom(student.currentRoom)
    creatureDao.createFightWith(student.id, opponent)
    stageService.setFightNote(student, opponent.id, Durations.FightTurn)
  }

  def continueFight(student: StudentForUpdate, owls: Seq[String]): CompletedStage = {
    val opponent = creatureDao.findInFightWith(student.id)
    val spells = spellDao.load(student.id)
    val turnOutcome = Fight.computeTurn(student, opponent, spells, owls)
    turnOutcome match {
      case FightContinues(updStudent, updOpponent) =>
        stageService.setFightNote(updStudent, updOpponent.id, Durations.FightTurn)
        creatureDao.updateInFightWith(updStudent.id, updOpponent)
        CompletedStage()
      case StudentWon(updStudent, updOpponent) =>
        stageService.writeFightResultToDiary(updStudent, opponent.id, Stage.FightWon)
        creatureDao.removeFightWithStudentUpdatingSkill(updStudent.id, skillDelta = 1)
        enterNextRoom(updStudent)
        CompletedStage(updates = Seq(Resource.Diary, Resource.CreatureHandlingSkills))
      case StudentLost(updStudent, updOpponent) =>
        stageService.writeFightResultToDiary(updStudent, opponent.id, Stage.FightLost)
        creatureDao.removeFightWithStudentUpdatingSkill(updStudent.id, skillDelta = 0)
        enterInfirmary(updStudent)
        CompletedStage(updates = Seq(Resource.Diary))
    }
  }

  def enterNextRoom(student: StudentForUpdate): Unit = {
    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, TravelRadius)
    val attendance = lessonDao.loadPartialAttendance(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
    val destination = Travel.pickDestination(student, nearbyRooms, attendance)
    val updatedStudent = student.copy(currentRoom = destination.room)

    destination match {
      case AttendClass(_, lessonId) =>
        stageService.setLessonNote(updatedStudent, lessonId, Durations.Study)
      case VisitClub(_, clubId) =>
        stageService.setClubNote(updatedStudent, clubId, Durations.Club)
      case VisitLibrary(newRoom) =>
        libraryService.commitLibraryVisit(updatedStudent)
        stageService.setGenericStageNote(updatedStudent, Stage.Library, Durations.Library)
      case ContinueTravelling(newRoom) =>
        stageService.setGenericStageNote(student.copy(currentRoom = newRoom), Stage.Travel, Durations.Travel)
    }
  }

  def enterInfirmary(student: StudentForUpdate): Unit = {
    val infirmary = roomDao.findInfirmaryNumber(student.level)
    stageService.setGenericStageNote(student.copy(currentRoom = infirmary), Stage.Infirmary, Durations.Infirmary(student))
  }
}
