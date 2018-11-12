package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}

import db.DbCtx

case class User(id: Long, email: String, loginInfo: LoginInfo) extends Identity

class Users(val db: DbCtx) {
  import db._

  val users = quote(querySchema[User]("users"))

 // def create(user: User) = user.copy(
 //   id = run(users.insert(lift(user)).returning(_.id)))
}

class LoginInfos(val db: DbCtx) {
  import db._

  private val q = quote(querySchema[LoginInfo]("login_info",
    _.providerID -> "provider_id", _.providerKey -> "provider_key"))

  def create(li: LoginInfo): LoginInfo = { run(q.insert(lift(li))); li }

  def find(li: LoginInfo): Option[LoginInfo] = run(
    q.filter(r => r.providerID == lift(li.providerID) && r.providerKey == lift(li.providerKey))
  ).headOption
}
