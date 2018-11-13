package models

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import db.DbCtx
import utils.FutureOption

import scala.concurrent.{ExecutionContext, Future}

case class UserLoginInfo(userId: Long, providerId: String, providerKey: String, id: Long = 0)

class UserLoginInfoDao(val db: DbCtx, implicit val ec: ExecutionContext) {
  import db._

  private val schema = quote(querySchema[UserLoginInfo]("user_login_info"))

  val repository: AuthInfoRepository = new DelegableAuthInfoRepository(
    new UserPasswordInfoDao(db, this, ec)
  )

  def create(li: UserLoginInfo): Future[UserLoginInfo] = Future {
    li.copy(id = run(schema.insert(lift(li)).returning(_.id)))
  }

  def findByLoginInfo(li: LoginInfo): FutureOption[UserLoginInfo] = Future {
    run(filterByLoginInfo(li)).headOption
  }

  def findIdByLoginInfo(li: LoginInfo): FutureOption[Long] = Future {
    run(filterByLoginInfo(li).map(_.id)).headOption
  }

  def findUserIdByLoginInfo(li: LoginInfo): FutureOption[Long] = Future {
    run(filterByLoginInfo(li).map(_.userId)).headOption
  }

  @inline private def filterByLoginInfo(li: LoginInfo) =
    quote(schema.filter(r => r.providerId == lift(li.providerID) && r.providerKey == lift(li.providerKey)))
}
