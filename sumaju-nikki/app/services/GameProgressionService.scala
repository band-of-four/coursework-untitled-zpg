package services

import gamelogic.Travel
import models.{Student, StudentDao, LessonDao, RoomDao}
import models.Student.{StageClub, StageFight, StageTravel}

class GameProgressionService(val studentDao: StudentDao,
                             val roomDao: RoomDao,
                             val lessonDao: LessonDao) {
  def pendingUpdates(count: Int): Seq[Student] =
    studentDao.findPendingTurnUpdates(count)

  def performUpdate(student: Student): Unit = {
    student.stage match {
      case StageTravel => finishTravelling(student)
      case _ => ???
    }
  }

  def finishTravelling(student: Student): Unit = {
    val nearbyRooms = roomDao.preloadInRadius(student.currentRoom, Travel.TravelRadius)
    val attendance = lessonDao.buildAttendanceMap(student.id, nearbyRooms.flatMap(_.lesson.map(_.id)))
    val nextRoom = Travel.pickNextRoom(student, nearbyRooms, attendance)


  }
}
