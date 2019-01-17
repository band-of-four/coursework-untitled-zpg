package models

import db.{DbCtx, Pagination}

case class StudentRelationship(studentA: Long, studentB: Long, relationship: Int, delta: Int)

case class StudentLetter(senderId: Long, receiverId: Long)

case class StudentRelationshipPreloaded(studentName: String, relationship: Int, delta: Int)

class StudentRelationshipDao(db: DbCtx) {
  import db._

  def createLetter(senderId: Long, receiverId: Long): Unit =
    run(query[StudentLetter].insert(lift(StudentLetter(senderId, receiverId))))

  def updateInClub(studentId: Long): Unit =
    run(infix"""SELECT student_relationships_update_in_club(${lift(studentId)})""".as[Insert[Unit]])

  def loadForStudent(studentId: Long, pagination: Pagination): Seq[StudentRelationshipPreloaded] = {
    /* Could be replaced with UPDATE RETURNING, https://github.com/getquill/quill/issues/572 */
    val relationships = run(
      query[StudentRelationship]
        .filter(sr => sr.studentA == lift(studentId) || sr.studentB == lift(studentId))
        .join(query[Student]).on {
          case (sr, s) => s.id != lift(studentId) && (s.id == sr.studentA || s.id == sr.studentB)
        }
        .map {
          case (sr, s) => ((sr.studentA, sr.studentB), StudentRelationshipPreloaded(s.name, sr.relationship, sr.delta))
        }
        .paginate(lift(pagination))
    )
    relationships.foreach { case ((studentA, studentB), _) =>
      run(
        query[StudentRelationship]
        .filter(sr => sr.studentA == lift(studentA) && sr.studentB == lift(studentB))
        .update(_.delta -> 0)
      )
    }
    relationships.map(_._2)
  }
}
