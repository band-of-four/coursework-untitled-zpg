package models

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import db.DbCtx

import scala.concurrent.ExecutionContext

case class UserLoginInfo(userId: Long, providerId: String, providerKey: String, id: Long = -1) {
  def toLoginInfo: LoginInfo = LoginInfo(providerId, providerKey)
}

class UserLoginInfoDao(val db: DbCtx, val ec: ExecutionContext) {
  import db._

  private val schema = quote(querySchema[UserLoginInfo]("user_login_info"))

  val repository: AuthInfoRepository = new DelegableAuthInfoRepository(
    new UserPasswordInfoDao(db, this, ec)
  )(ec)

  def create(uli: UserLoginInfo): UserLoginInfo =
    uli.copy(id = run(schema.insert(lift(uli)).returning(_.id)))

  def createForUser(user: User, li: LoginInfo): UserLoginInfo =
    create(UserLoginInfo(user.id, li.providerID, li.providerKey))

  def findByUserId(userId: Long): Option[UserLoginInfo] =
    run(schema.filter(_.userId == lift(userId))).headOption

  def findByLoginInfo(li: LoginInfo): Option[UserLoginInfo] =
    run(filterByLoginInfo(li)).headOption

  def findIdByLoginInfo(li: LoginInfo): Option[Long] =
    run(filterByLoginInfo(li).map(_.id)).headOption

  def findUserIdByLoginInfo(li: LoginInfo): Option[Long] =
    run(filterByLoginInfo(li).map(_.userId)).headOption

  @inline private def filterByLoginInfo(li: LoginInfo) =
    quote(schema.filter(r => r.providerId == lift(li.providerID) && r.providerKey == lift(li.providerKey)))
}
