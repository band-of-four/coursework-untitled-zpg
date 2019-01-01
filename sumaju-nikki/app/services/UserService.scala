package services

import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.impl.providers.{CredentialsProvider, SocialProfile}
import models._
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import com.mohiva.play.silhouette.persistence.repositories.DelegableAuthInfoRepository
import db.DbCtx
import org.postgresql.util.PSQLException
import play.api.Configuration
import services.UserService.EmailAlreadyTakenException

import scala.concurrent.{ExecutionContext, Future}

object UserService {
  class EmailAlreadyTakenException extends RuntimeException
}

class UserService(val userDao: UserDao,
                  val loginInfoDao: UserLoginInfoDao,
                  val db: DbCtx,
                  implicit val ec: ExecutionContext,
                  config: Configuration) extends IdentityService[User] {
  val repository: AuthInfoRepository = new DelegableAuthInfoRepository(
    new UserPasswordInfoDao(db, loginInfoDao),
    new UserOAuth2InfoDao(db, loginInfoDao)
  )

  val passwordRegistry: PasswordHasherRegistry = {
    val bcryptRounds = config.get[Int]("auth.bcrypt-rounds")
    PasswordHasherRegistry(new BCryptPasswordHasher(bcryptRounds))
  }

  override def retrieve(li: LoginInfo): Future[Option[User]] = Future {
    for {
      userId <- loginInfoDao.findUserIdByLoginInfo(li)
      user <- userDao.findById(userId)
    } yield user
  }

  def saveEmail(email: String, password: String): Future[User] =
    Future {
      val passwordInfo = passwordRegistry.current.hash(password)
      db.transaction {
        val user = userDao.create(User(Some(email)))
        val userLoginInfo = loginInfoDao.createForUser(user, LoginInfo(CredentialsProvider.ID, email))
        val authInfo = repository.add(userLoginInfo.toLoginInfo, passwordInfo)
        user
      }
    } recoverWith {
      case e: PSQLException =>
        if (e.getMessage.startsWith("ERROR: duplicate key value"))
          Future.failed(new EmailAlreadyTakenException())
        else
          Future.failed(e)
    }
}
