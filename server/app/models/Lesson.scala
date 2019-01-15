package models

import db.DbCtx
import models.LessonDao.PartialAttendanceMap

case class Lesson(name: String, level: Int, requiredAttendance: Int, id: Long = -1)

case class LessonAttendance(lessonId: Long, studentId: Long, classesAttended: Int)

case class LessonAttendancePreloaded(name: String, requiredAttendance: Int, attended: Int)

object LessonDao {
  type PartialAttendanceMap = Map[Long, Int]
}

class LessonDao(val db: DbCtx) {
  import db._

  def getLessons(s: Student): Seq[Lesson] =
    run(query[Lesson].filter(_.level == lift(s.level)))

  def loadAttendance(studentId: Long, studentLevel: Int): Seq[LessonAttendancePreloaded] =
    run(
      query[Lesson]
        .filter(_.level == lift(studentLevel))
        .leftJoin(query[LessonAttendance]).on {
          case (l, la) => la.lessonId == l.id && la.studentId == lift(studentId)
        }
        .map {
          case (l, la) => LessonAttendancePreloaded(l.name, l.requiredAttendance, la.map(_.classesAttended).getOrElse(0))
        }
    )

  def loadPartialAttendance(studentId: Long, lessonIds: Seq[Long]): PartialAttendanceMap =
    run(
      query[LessonAttendance]
        .filter(a => a.studentId == lift(studentId) && lift(lessonIds).contains(a.lessonId))
        .map(a => (a.lessonId, a.classesAttended))
    ).toMap

  def updateAttendance(studentId: Long): Unit =
    run(infix"""SELECT lesson_attendance_update_at_lesson_end(${lift(studentId)})""".as[Insert[Unit]])
}
