package game

import java.time.Duration

import models.LessonDao.LessonAttendanceMap
import models.{RoomPreloaded, Student}
import utils.WeightedSample

object Travel {
  val TravelRadius = 10
  val TravelDuration = Duration.ofSeconds(90)

  sealed trait Destination {
    def room: Long
  }

  case class AttendClass(room: Long) extends Destination
  case class VisitClub(room: Long) extends Destination
  case class ContinueTravelling(room: Long) extends Destination

  def pickDestination(student: Student, rooms: Seq[RoomPreloaded], attendance: LessonAttendanceMap): Destination = {
    val nextRoom = WeightedSample(rooms) {
      case RoomPreloaded(_, level, _, _) if level > student.academicYear =>
        0
      case RoomPreloaded(_, level, Some(club), _) =>
        0.15
      case RoomPreloaded(_, _, _, Some(lesson)) =>
        0.5 * (lesson.requiredAttendance - attendance.getOrElse(lesson.id, 0))
      case RoomPreloaded(_, _, None, None) =>
        ??? // library?
    }

    nextRoom match {
      case Some(room) if room.lesson.isDefined =>
        AttendClass(room.number)
      case Some(room) if room.club.isDefined =>
        VisitClub(room.number)
      case Some(room) =>
        ??? // Library
      case None =>
        val roomsByLevel = rooms.sortBy(c => (c.level, c.number))
        ContinueTravelling(
          if (roomsByLevel.head.level <= student.academicYear)
            roomsByLevel.head.number // travel further (higher room numbers => higher academic year)
          else
            roomsByLevel.last.number // travel back (lower room numbers => lower academic year)
        )
    }
  }
}
