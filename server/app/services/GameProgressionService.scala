package services

import models._
import models.Student.Stage
import game.{Durations, Fight, GameProgressionResource => Resource, Travel}
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
            enterNextRoom(student)
            CompletedStage()
          case Stage.Club =>
            relationshipDao.updateInClub(student.id)
            stageService.writeStageNoteToDiary(student)
            stageService.setGenericStageNote(student, Stage.Travel, Durations.Travel)
            CompletedStage(updates = Seq(Resource.Diary, Resource.Relationships))
          case Stage.Lesson =>
            lessonDao.updateAttendance(student.id)
            stageService.writeStageNoteToDiary(student)
            stageService.setGenericStageNote(student, Stage.Travel, Durations.Travel)
            CompletedStage(updates = Seq(Resource.Diary, Resource.LessonAttendance))
          case Stage.Library =>
            libraryService.commitVisitEnd(student)
            stageService.setGenericStageNote(student, Stage.Travel, Durations.Travel)
            CompletedStage(updates = Seq(Resource.Spells))
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
      case FightContinues(_, opponent) =>
        stageService.setFightNote(student, opponent.id, Durations.FightTurn)
        creatureDao.updateInFightWith(student.id, opponent)
        CompletedStage()
      case StudentWon(student, opponent) =>
        stageService.writeFightResultToDiary(student, opponent.id, Stage.FightWon)
        creatureDao.removeFightWithStudentUpdatingSkill(student.id, skillDelta = 1)
        enterNextRoom(student)
        CompletedStage(updates = Seq(Resource.Diary, Resource.CreatureHandlingSkills))
      case StudentLost(student, opponent) =>
        stageService.writeFightResultToDiary(student, opponent.id, Stage.FightLost)
        creatureDao.removeFightWithStudentUpdatingSkill(student.id, skillDelta = 0)
        enterInfirmary(student)
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
    val infirmary = roomDao.findClosest(Room.Kind.Infirmary, student.currentRoom)
    stageService.setGenericStageNote(student.copy(currentRoom = infirmary), Stage.Infirmary, Durations.Infirmary(student))
  }
}
