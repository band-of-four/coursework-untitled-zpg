package services

import game.{Fight, Heal, Travel}
import models._
import models.Student.{StageFight, StageStudy, StageTravel}
import models.Room.Kind._
import game.Fight._
import game.Travel._
import game.Study.StudyDuration
import utils.RandomEvent

class GameProgressionService(val studentDao: StudentDao,
                             val roomDao: RoomDao,
                             val lessonDao: LessonDao,
                             val creatureDao: CreatureDao,
                             val spellDao: SpellDao) {
  def pendingUpdates(count: Int): Seq[Student] =
    studentDao.findPendingTurnUpdates(count)

  def performUpdate(student: Student): Unit =
    student.stage match {
      case StageTravel =>
        if (RandomEvent(FightChance)) startFighting(student)
        else enterNextRoom(student)
      case StageFight => continueFighting(student)
      case StageStudy => finishStudying(student)
    }

  def finishStudying(student: Student): Unit = ???

  def startFighting(student: Student): Unit = {
    val opponent = creatureDao.findNearRoom(student.currentRoom)
    creatureDao.createFight(student, opponent)
    studentDao.updateStage(student.id, student.currentRoom, StageFight, FightTurnDuration)
  }

  def continueFighting(student: Student): Unit = {
    val opponent = creatureDao.findInFight(student)
    val turnOutcome = Fight.computeTurn(student, opponent, spells = spellDao.findLearned(student.id))

    turnOutcome match {
      case StudentWon =>
        creatureDao.removeFight(student)
      case StudentLost =>
        val infirmary = roomDao.findClosest(Infirmary, student.currentRoom)
        studentDao.updateAfterLostFight(student, Heal.duration(student), infirmary)
        creatureDao.removeFight(student)
      case FightContinues(studentHp, creatureHp) =>
        creatureDao.updateInFight(student, creatureHp)
        studentDao.updateInFight(student, studentHp, FightTurnDuration)
    }
  }

  def enterNextRoom(student: Student): Unit = {
    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, TravelRadius)
    val attendance = lessonDao.buildAttendanceMap(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
    Travel.pickDestination(student, nearbyRooms, attendance) match {
      case AttendClass(newRoom) =>
        studentDao.updateStage(student.id, newRoom, StageStudy, StudyDuration)
      case VisitClub(newRoom) =>
        ???
      case ContinueTravelling(newRoom) =>
        studentDao.updateStage(student.id, newRoom, StageTravel, TravelDuration)
    }
  }
}
