package services

import models._
import game.{Fight, Heal, Travel}
import game.Fight._
import game.Travel._
import game.Study._
import services.StageService.StageUpdate
import utils.RandomEvent

class GameProgressionService(stageService: StageService,
                             owlService: OwlService,
                             roomDao: RoomDao,
                             lessonDao: LessonDao,
                             creatureDao: CreatureDao,
                             spellDao: SpellDao) {
  def pendingUpdates(count: Int): Seq[StudentForUpdate] =
    stageService.findPendingUpdates(count)

  def updateStage(student: StudentForUpdate): StageUpdate =
    stageService.transactionalUpdate(student.id) {
      owlService.useActiveOwlsForUpdate(student) { owls =>
        student.stage match {
          case Student.Stage.Travel if RandomEvent(FightChance) =>
            startFight(student)
          case Student.Stage.Travel =>
            enterNextRoom(student)
          case Student.Stage.Fight =>
            continueFight(student)
          case Student.Stage.Lesson =>
            stageService.commitTravelStage(student, TravelDuration)
        }
      }
    }

  def startFight(student: StudentForUpdate): Unit = {
    val opponent = creatureDao.findNearRoom(student.currentRoom)
    creatureDao.createFightWith(student.id, opponent)
    stageService.commitFight(student, opponent.id, FightTurnDuration)
  }

  def continueFight(student: StudentForUpdate): Unit = {
    val opponent = creatureDao.findInFightWith(student.id)
    val spells = spellDao.findLearned(student.id)
    val turnOutcome = Fight.computeTurn(student, opponent, spells)
    stageService.commitFightStage(turnOutcome, FightTurnDuration)
    turnOutcome match {
      case FightContinues(_, creature) =>
        creatureDao.updateInFightWith(student.id, creature)
      case StudentWon(student, _) =>
        creatureDao.removeFightWith(student.id)
        enterNextRoom(student)
      case StudentLost(student, _) =>
        creatureDao.removeFightWith(student.id)
        enterInfirmary(student)
    }
  }

  def enterNextRoom(student: StudentForUpdate): Unit = {
    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, TravelRadius)
    val attendance = lessonDao.buildAttendanceMap(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
    Travel.pickDestination(student, nearbyRooms, attendance) match {
      case AttendClass(newRoom, lessonId) =>
        stageService.commitLessonStage(student.copy(currentRoom = newRoom), lessonId, StudyDuration)
      case VisitClub(newRoom, clubId) =>
        ???
      case ContinueTravelling(newRoom) =>
        stageService.commitTravelStage(student.copy(currentRoom = newRoom), TravelDuration)
    }
  }

  def enterInfirmary(student: StudentForUpdate): Unit = {
    val infirmary = roomDao.findClosest(Room.Kind.Infirmary, student.currentRoom)
    stageService.commitInfirmaryStage(student.copy(currentRoom = infirmary), Heal.duration(student))
  }
}
