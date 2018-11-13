package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import db.DbCtx
import scala.concurrent.{ExecutionContext, Future}

case class User(id: Long, email: String, loginInfo: LoginInfo) extends Identity

class UserDao(val db: DbCtx) {
  import db._

  private val q = quote(querySchema[User]("users"))

 // def create(user: User) = user.copy(
 //   id = run(users.insert(lift(user)).returning(_.id)))
}

class LoginInfoDao(val db: DbCtx, implicit val ec: ExecutionContext) {
  import db._

  private val q = quote(querySchema[LoginInfo]("login_info",
    _.providerID -> "provider_id", _.providerKey -> "provider_key"))

  def create(li: LoginInfo): Future[LoginInfo] = Future {
    run(q.insert(lift(li)))
    li
  }

  def find(li: LoginInfo): Future[Option[LoginInfo]] = Future { run(
      q.filter(r => r.providerID == lift(li.providerID) && r.providerKey == lift(li.providerKey))
    ).headOption
  }
}
