package models

import db.DbCtx

case class StudentLetter(senderId: Long, receiverId: Long)

class StudentLetterDao(db: DbCtx) {
  import db._

  def insert(senderId: Long, receiverId: Long): Unit =
    run(query[StudentLetter].insert(lift(StudentLetter(senderId, receiverId))))
}
