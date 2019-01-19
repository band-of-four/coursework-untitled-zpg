package services

import models._
import game.{Durations, Fight, Travel}
import game.Fight._
import game.Travel._
import services.GameProgressionService._
import services.StageService.StageUpdate
import utils.RandomEvent

object GameProgressionService {
  sealed trait StageCompletion
  case class CompletedLesson(attendance: Seq[LessonAttendancePreloaded]) extends StageCompletion
  case class CompletedLibrary(spells: Seq[SpellPreloaded]) extends StageCompletion
  case object CompletedGenericStage extends StageCompletion
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
          case Student.Stage.Fight =>
            continueFight(student, owls)
            CompletedGenericStage
          case Student.Stage.Travel if RandomEvent(FightChance) =>
            startFight(student)
            CompletedGenericStage
          case Student.Stage.Travel =>
            enterNextRoom(student)
            CompletedGenericStage
          case Student.Stage.Club =>
            relationshipDao.updateInClub(student.id)
            stageService.commitTravelStage(student, Durations.Travel)
            CompletedGenericStage
          case Student.Stage.Lesson =>
            lessonDao.updateAttendance(student.id)
            val newAttendance = lessonDao.loadAttendance(student.id, student.level)
            stageService.commitTravelStage(student, Durations.Travel)
            CompletedLesson(newAttendance)
          case Student.Stage.Library =>
            libraryService.commitVisitEnd(student)
            val newSpells = spellDao.load(student.id)
            stageService.commitTravelStage(student, Durations.Travel)
            CompletedLibrary(newSpells)
        }
      }
    }

  def startFight(student: StudentForUpdate): Unit = {
    val opponent = creatureDao.findNearRoom(student.currentRoom)
    creatureDao.createFightWith(student.id, opponent)
    stageService.commitFight(student, opponent.id, Durations.FightTurn)
  }

  def continueFight(student: StudentForUpdate, owls: Seq[String]): Unit = {
    val opponent = creatureDao.findInFightWith(student.id)
    val spells = spellDao.load(student.id)
    val turnOutcome = Fight.computeTurn(student, opponent, spells, owls)
    stageService.commitFightStage(turnOutcome, Durations.FightTurn)
    turnOutcome match {
      case FightContinues(_, creature) =>
        creatureDao.updateInFightWith(student.id, creature)
      case StudentWon(student, _) =>
        creatureDao.removeFightWithStudentUpdatingSkill(student.id, skillDelta = 1)
        enterNextRoom(student)
      case StudentLost(student, _) =>
        creatureDao.removeFightWithStudentUpdatingSkill(student.id, skillDelta = 0)
        enterInfirmary(student)
    }
  }

  def enterNextRoom(student: StudentForUpdate): Unit = {
    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, TravelRadius)
    val attendance = lessonDao.loadPartialAttendance(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
    val destination = Travel.pickDestination(student, nearbyRooms, attendance)
    val updatedStudent = student.copy(currentRoom = destination.room)

    destination match {
      case AttendClass(_, lessonId) =>
        stageService.commitLessonStage(updatedStudent, lessonId, Durations.Study)
      case VisitClub(_, clubId) =>
        stageService.commitClubStage(updatedStudent, clubId, Durations.Club)
      case VisitLibrary(newRoom) =>
        libraryService.commitLibraryVisit(updatedStudent)
        stageService.commitLibraryStage(updatedStudent, Durations.Library)
      case ContinueTravelling(newRoom) =>
        stageService.commitTravelStage(student.copy(currentRoom = newRoom), Durations.Study)
    }
  }

  def enterInfirmary(student: StudentForUpdate): Unit = {
    val infirmary = roomDao.findClosest(Room.Kind.Infirmary, student.currentRoom)
    stageService.commitInfirmaryStage(student.copy(currentRoom = infirmary), Durations.Infirmary(student))
  }
}
