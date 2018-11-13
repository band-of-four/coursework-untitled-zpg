package models

import com.mohiva.play.silhouette.api.Identity
import db.DbCtx

case class User(email: String, id: Long = -1) extends Identity

class UserDao(val db: DbCtx) {
  import db._

  private val schema = quote(querySchema[User]("users"))

  def create(user: User): User =
    user.copy(id = run(schema.insert(lift(user)).returning(_.id)))

  def findById(id: Long): Option[User] =
    run(schema.filter(_.id == lift(id))).headOption

  def findByEmail(email: String): Option[User] =
    run(schema.filter(_.email == lift(email))).headOption
}
