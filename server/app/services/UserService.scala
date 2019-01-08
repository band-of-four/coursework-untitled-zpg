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

  def saveEmail(email: String, password: String): Future[User] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, email)
    for {
      user <- saveUser(User(Some(email)), loginInfo)
      passwordInfo <- Future { passwordRegistry.current.hash(password) }
      authInfo <- repository.add(loginInfo, passwordInfo)
    } yield user
  }

  def saveSocial(profile: SocialProfile, authInfo: AuthInfo): Future[User] =
    for {
      existingUser <- Future { userDao.findByLoginInfo(profile.loginInfo) }
      user <- existingUser match {
        case Some(user) => Future.successful(user)
        case _ => saveUser(User(email = None), profile.loginInfo)
      }
      authInfo <- repository.save(profile.loginInfo, authInfo)
    } yield user

  private def saveUser(user: User, loginInfo: LoginInfo): Future[User] =
    Future {
      db.transaction {
        val dbUser = userDao.create(user)
        loginInfoDao.createForUser(dbUser, loginInfo)
        dbUser
      }
    } recoverWith {
      case e: PSQLException =>
        if (e.getMessage.startsWith("ERROR: duplicate key value"))
          Future.failed(new EmailAlreadyTakenException())
        else
          Future.failed(e)
    }
}
