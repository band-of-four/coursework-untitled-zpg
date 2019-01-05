package models

import db.DbCtx
import models.LessonDao.LessonAttendanceMap

case class Lesson(name: String, academicYear: Int, requiredAttendance: Int, id: Long = -1)

case class LessonAttendance(lessonId: Long, studentId: Long, classesAttended: Int)

object LessonDao {
  type LessonAttendanceMap = Map[Long, Int]
}

class LessonDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Lesson]("lessons"))
  private val attendanceSchema = quote(querySchema[LessonAttendance]("lesson_attendance"))

  def getLessons(s: Student): Seq[Lesson] =
    run(schema.filter(_.academicYear == lift(s.academicYear)))

  def buildAttendanceMap(studentId: Long, lessonIds: Seq[Long]): LessonAttendanceMap =
    run(
      attendanceSchema
        .filter(a => a.studentId == lift(studentId) && lift(lessonIds).contains(a.lessonId))
        .map(a => (a.lessonId, a.classesAttended))
    ).toMap
}
