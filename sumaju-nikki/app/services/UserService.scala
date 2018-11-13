package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.{User, UserDao, UserLoginInfoDao}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import db.DbCtx
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

class UserService(val userDao: UserDao,
                  val loginInfoDao: UserLoginInfoDao,
                  val db: DbCtx,
                  implicit val ec: ExecutionContext,
                  config: Configuration) extends IdentityService[User] {

  private val bcryptRounds = config.get[Int]("auth.bcrypt-rounds")
  private val passwordRegistry = PasswordHasherRegistry(new BCryptPasswordHasher(bcryptRounds))

  override def retrieve(li: LoginInfo): Future[Option[User]] =
    for {
      userId <- loginInfoDao.findUserIdByLoginInfo(li)
      user <- userDao.findById(userId)
    } yield user

  def saveWithPassword(user: User, password: String): Future[User] = Future {
    db.transaction {
      val passwordInfo = passwordRegistry.current.hash(password)
      for {
        user <- userDao.create(user)
        userLoginInfo <- loginInfoDao.createForUser(
          user, LoginInfo(CredentialsProvider.ID, user.email)
        )
        authInfo <- loginInfoDao.repository.add(
          userLoginInfo.toLoginInfo, passwordInfo
        )
      } yield user
    }
  }.flatten
}
