package models

import db.DbCtx

case class User(id: Long, name: String, passwordHash: String)

class Users(val db: DbCtx) {
  import db._

  val users = quote(querySchema[User]("users"))

  def create(user: User) = user.copy(
    id = run(users.insert(lift(user)).returning(_.id)))
}
