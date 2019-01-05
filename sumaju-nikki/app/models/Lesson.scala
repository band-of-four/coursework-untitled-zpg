package models

import db.DbCtx
import scala.util.Random

case class Lesson(name: String, academic_year: Int)

class LessonDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[Lesson]("lessons"))

  def getLessonsOfCharacter(c: Character): Seq[Lesson] =
    run(schema.filter(_.academic_year == lift(c.academic_year)))

}
