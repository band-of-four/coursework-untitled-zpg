package services

import game.Travel
import models.{LessonDao, RoomDao, Student, StudentDao}
import models.Student.{StageTravel, StageFight, StageStudy}
import game.Fight.FightChance
import utils.RandomEvent

class GameProgressionService(val studentDao: StudentDao,
                             val roomDao: RoomDao,
                             val lessonDao: LessonDao) {
  def pendingUpdates(count: Int): Seq[Student] =
    studentDao.findPendingTurnUpdates(count)

  def performUpdate(student: Student): Unit =
    student.stage match {
      case StageTravel => finishTravelling(student)
      case StageFight => continueFighting(student)
      case StageStudy => finishStudying(student)
    }

  def finishTravelling(student: Student): Unit = {
    if (RandomEvent(FightChance))
      startFighting(student)
    else {
      val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, Travel.TravelRadius)
      val attendance = lessonDao.buildAttendanceMap(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
      val nextRoom = Travel.pickNextRoom(student, nearbyRooms, attendance)
    }
  }

  def finishStudying(student: Student): Unit = ???

  def startFighting(student: Student): Unit = ???

  def continueFighting(student: Student): Unit = ???
}
