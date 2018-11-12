package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

import db.DbCtx

case class User(
  id: Long,
  email: String,
  loginInfo: LoginInfo
) extends Identity

class Users(val db: DbCtx) {
  import db._

  val users = quote(querySchema[User]("users"))

 // def create(user: User) = user.copy(
 //   id = run(users.insert(lift(user)).returning(_.id)))
}
