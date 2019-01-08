package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import db.DbCtx

case class User(email: Option[String], id: Long = -1) extends Identity

class UserDao(val db: DbCtx) {
  import db._

  def create(user: User): User =
    user.copy(id = run(query[User].insert(lift(user)).returning(_.id)))

  def findById(id: Long): Option[User] =
    run(query[User].filter(_.id == lift(id))).headOption

  def findByEmail(email: String): Option[User] =
    run(query[User].filter(_.email.exists(_ == lift(email)))).headOption

  def findByLoginInfo(loginInfo: LoginInfo): Option[User] =
    run(
      query[User]
        .join(query[UserLoginInfo])
        .on((u, li) => u.id == li.userId &&
          li.providerId == lift(loginInfo.providerID) &&
          li.providerKey == lift(loginInfo.providerKey))
        .map(_._1)
    ).headOption
}
