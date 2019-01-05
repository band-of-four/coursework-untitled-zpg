package models

import db.DbCtx
import models.LessonDao.LessonAttendanceMap

case class Lesson(name: String, academicYear: Int, requiredAttendance: Int, id: Long = -1)

case class LessonAttendance(lessonId: Long, characterId: Long, classesAttended: Int)

object LessonDao {
  type LessonAttendanceMap = Map[Long, Int]
}

class LessonDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Lesson]("lessons"))
  private val attendanceSchema = quote(querySchema[LessonAttendance]("lesson_attendance"))

  def getLessonsOfCharacter(c: Character): Seq[Lesson] =
    run(schema.filter(_.academicYear == lift(c.academicYear)))

  def buildAttendanceMap(characterId: Long, lessonIds: Seq[Long]): LessonAttendanceMap =
    run(
      attendanceSchema
        .filter(a => a.characterId == lift(characterId) && lift(lessonIds).contains(a.lessonId))
        .map(a => (a.lessonId, a.classesAttended))
    ).toMap
}
