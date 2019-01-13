package services

import models._
import game.Fight
import game.Fight._
import utils.RandomEvent

class GameProgressionService(stageService: StageService,
                             studentDao: StudentDao,
                             roomDao: RoomDao,
                             lessonDao: LessonDao,
                             creatureDao: CreatureDao,
                             spellDao: SpellDao) {
  def pendingUpdates(count: Int): Seq[StudentForUpdate] =
    studentDao.findPendingStageUpdate(count)

  def performUpdate(student: StudentForUpdate): Unit =
    student.stage match {
      case Student.Stage.Travel =>
        if (RandomEvent(FightChance)) startFighting(student)
        else enterNextRoom(student)
      case Student.Stage.Fight => continueFighting(student)
      case Student.Stage.Lesson => finishStudying(student)
    }

  def startFighting(student: StudentForUpdate): Unit = {
    val opponent = creatureDao.findNearRoom(student.currentRoom)
    creatureDao.createFightWith(student.id, opponent)
    stageService.commitFight(student, opponent.id)
  }

  def continueFighting(student: StudentForUpdate): Unit = {
    val opponent = creatureDao.findInFightWith(student.id)
    val spells = spellDao.findLearned(student.id)
    val turnOutcome = Fight.computeTurn(student, opponent, spells)
    stageService.commitFightStage(turnOutcome)
  }

  def finishStudying(student: StudentForUpdate): Unit = ???

  def enterNextRoom(student: StudentForUpdate): Unit = ???
//    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, TravelRadius)
//    val attendance = lessonDao.buildAttendanceMap(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
//    Travel.pickDestination(student, nearbyRooms, attendance) match {
//      case AttendClass(newRoom) =>
//        studentDao.updateStage(student.id, newRoom, Student.Stage.Lesson, StudyDuration)
//      case VisitClub(newRoom) =>
//        ???
//      case ContinueTravelling(newRoom) =>
//        studentDao.updateStage(student.id, newRoom, Student.Stage.Travel, TravelDuration)
//    }
}
