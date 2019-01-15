package models

import db.DbCtx

case class StudentLibraryVisit(studentId: Long, acquiringSpellId: Long)

class StudentLibraryVisitDao(db: DbCtx) {
  import db._

  def createVisit(studentId: Long, spellId: Long): Unit =
    run(query[StudentLibraryVisit].insert(lift(StudentLibraryVisit(studentId, spellId))))

  def endVisit(studentId: Long): Unit =
    run(infix"""SELECT student_library_visit_end(${lift(studentId)})""".as[Insert[Unit]])
}

