package services

import game.Travel
import models.{LessonDao, RoomDao, Student, StudentDao}
import models.Student.{StageFight, StageStudy, StageTravel}
import game.Fight.FightChance
import game.Travel.{TravelDuration, TravelRadius, AttendClass, ContinueTravelling, VisitClub}
import game.Study.StudyDuration
import utils.RandomEvent

class GameProgressionService(val studentDao: StudentDao,
                             val roomDao: RoomDao,
                             val lessonDao: LessonDao) {
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

  def startFighting(student: Student): Unit = ???

  def continueFighting(student: Student): Unit = ???

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
