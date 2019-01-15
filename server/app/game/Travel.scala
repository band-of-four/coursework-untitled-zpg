package game

import models.LessonDao.PartialAttendanceMap
import models.{RoomPreloaded, StudentForUpdate}
import models.Room.Kind._
import utils.WeightedSample

object Travel {
  val TravelRadius = 10

  sealed trait Destination {
    def room: Long
  }

  case class AttendClass(room: Long, lessonId: Long) extends Destination
  case class VisitClub(room: Long, clubId: Long) extends Destination
  case class VisitLibrary(room: Long) extends Destination
  case class ContinueTravelling(room: Long) extends Destination

  def pickDestination(student: StudentForUpdate, rooms: Seq[RoomPreloaded], attendance: PartialAttendanceMap): Destination = {
    val nextRoom = WeightedSample(rooms) {
      case RoomPreloaded(_, level, _, _, _) if level > student.level =>
        0
      case RoomPreloaded(_, level, Clubroom, Some(club), _) =>
        0.3
      case RoomPreloaded(_, _, Classroom, _, Some(lesson)) =>
        0.5 * (lesson.requiredAttendance - attendance.getOrElse(lesson.id, 0))
      case RoomPreloaded(_, _, Library, _, _) =>
        0.05
      case RoomPreloaded(_, _, Infirmary, _, _) =>
        0
    }

    nextRoom match {
      case Some(room) if room.kind == Classroom =>
        AttendClass(room.number, room.lesson.get.id)
      case Some(room) if room.kind == Clubroom =>
        VisitClub(room.number, room.club.get.id)
      case Some(room) if room.kind == Library =>
        VisitLibrary(room.number)
      case _ =>
        val roomsByLevel = rooms.sortBy(c => (c.level, c.number))
        ContinueTravelling(
          if (roomsByLevel.head.level <= student.level)
            roomsByLevel.head.number // travel further (higher room numbers => higher academic year)
          else
            roomsByLevel.last.number // travel back (lower room numbers => lower academic year)
        )
    }
  }
}
