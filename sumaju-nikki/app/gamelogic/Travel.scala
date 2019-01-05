package gamelogic

import models.LessonDao.LessonAttendanceMap
import models.{Character, RoomPreloaded}
import utils.WeightedRandomSampler

object Travel {
  val TravelRadius = 10

  def pickNextRoom(character: Character, rooms: Seq[RoomPreloaded], attendance: LessonAttendanceMap): Long = {
    val weightedRooms = WeightedRandomSampler(rooms) {
      case RoomPreloaded(_, Some(club), _) =>
        0.15
      case RoomPreloaded(_, _, Some(lesson)) if lesson.academicYear != character.academicYear =>
        0
      case RoomPreloaded(_, _, Some(lesson)) =>
        0.5 * (lesson.requiredAttendance - attendance.getOrElse(lesson.id, 0))
      case RoomPreloaded(_, None, None) =>
        0 // library?
    }
    val nextRoom = weightedRooms.sample
    nextRoom.number
  }
}
