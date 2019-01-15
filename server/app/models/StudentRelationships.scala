package models

import db.DbCtx

case class StudentRelationships(studentA: Long, studentB: Long, relationship: Int)

case class StudentLetter(senderId: Long, receiverId: Long)

class StudentRelationshipDao(db: DbCtx) {
  import db._

  def insert(senderId: Long, receiverId: Long): Unit =
    run(query[StudentLetter].insert(lift(StudentLetter(senderId, receiverId))))
}
