package models

import db.DbCtx

case class StudentRelationships(studentA: Long, studentB: Long, relationship: Int, delta: Int)

case class StudentLetter(senderId: Long, receiverId: Long)

class StudentRelationshipDao(db: DbCtx) {
  import db._

  def createLetter(senderId: Long, receiverId: Long): Unit =
    run(query[StudentLetter].insert(lift(StudentLetter(senderId, receiverId))))

  def updateInClub(studentId: Long): Unit =
    run(infix"""SELECT student_relationships_update_in_club(${lift(studentId)})""".as[Insert[Unit]])
}
