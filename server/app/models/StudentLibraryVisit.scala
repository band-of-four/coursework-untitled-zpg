package models

import db.DbCtx

case class StudentLibraryVisit(studentId: Long, acquiringSpellId: Long)

case class StudentLibraryVisitPreloaded(spellName: String, spellPower: Int)

class StudentLibraryVisitDao(db: DbCtx) {
  import db._

  def load(studentId: Long): StudentLibraryVisitPreloaded =
    run(
      query[StudentLibraryVisit]
        .filter(_.studentId == lift(studentId))
        .join(query[Spell]).on {
          case (slv, s) => s.id == slv.acquiringSpellId
        }
        .map {
          case (slv, s) => StudentLibraryVisitPreloaded(s.name, s.power)
        }
    ).head

  def createVisit(studentId: Long, spellId: Long): Unit =
    run(query[StudentLibraryVisit].insert(lift(StudentLibraryVisit(studentId, spellId))))

  def endVisit(studentId: Long): Unit =
    run(infix"""SELECT student_library_visit_end(${lift(studentId)})""".as[Query[String]])
}

