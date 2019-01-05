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
      case RoomPreloaded(_, Some(club), _) =>
        0.15
      case RoomPreloaded(_, _, Some(lesson)) if lesson.academicYear != student.academicYear =>
        0
      case RoomPreloaded(_, _, Some(lesson)) =>
        0.5 * (lesson.requiredAttendance - attendance.getOrElse(lesson.id, 0))
      case RoomPreloaded(_, None, None) =>
        0 // library?
    }

    nextRoom match {
      case Some(room) if room.lesson.isDefined =>
        AttendClass(room.number)
      case Some(room) if room.club.isDefined =>
        VisitClub(room.number)
      case Some(room) =>
        ??? // Library
      case None =>
        val classesByLevel = rooms.filter(_.lesson.isDefined).sortBy(c => (c.lesson.get.academicYear, c.number))

        val maxRoomLevel = classesByLevel.head.lesson.get.academicYear
        if (maxRoomLevel <= student.academicYear)
          ContinueTravelling(classesByLevel.head.number) // travel further (higher room numbers => higher academic year)
        else
          ContinueTravelling(classesByLevel.last.number) // travel back (lower room numbers => lower academic year)
    }
  }
}
