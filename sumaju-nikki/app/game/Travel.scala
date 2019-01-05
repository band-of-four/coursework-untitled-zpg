package game

import models.LessonDao.LessonAttendanceMap
import models.{RoomPreloaded, Student}
import utils.{RandomEvent, WeightedSample}

object Travel {
  val TravelRadius = 10

  def pickNextRoom(student: Student, rooms: Seq[RoomPreloaded], attendance: LessonAttendanceMap): RoomPreloaded =
    WeightedSample(rooms) {
      case RoomPreloaded(_, Some(club), _) =>
        0.15
      case RoomPreloaded(_, _, Some(lesson)) if lesson.academicYear != student.academicYear =>
        0
      case RoomPreloaded(_, _, Some(lesson)) =>
        0.5 * (lesson.requiredAttendance - attendance.getOrElse(lesson.id, 0))
      case RoomPreloaded(_, None, None) =>
        0 // library?
    }
}
