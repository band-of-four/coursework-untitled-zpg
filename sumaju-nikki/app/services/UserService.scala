package services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import models.{User, UserDao, UserLoginInfoDao}
import com.mohiva.play.silhouette.password.BCryptPasswordHasher
import play.api.Configuration

import scala.concurrent.{ExecutionContext, Future}

class UserService(val users: UserDao,
                  val loginInfoDao: UserLoginInfoDao,
                  config: Configuration,
                  implicit val ec: ExecutionContext) extends IdentityService[User] {

  private val bcryptRounds = config.get[Int]("auth.bcrypt-rounds")
  private val passwordRegistry = PasswordHasherRegistry(new BCryptPasswordHasher(bcryptRounds))

  override def retrieve(li: LoginInfo): Future[Option[User]] =
    for {
      userId <- loginInfoDao.findUserIdByLoginInfo(li)
      user <- users.findById(userId)
    } yield user

//  def saveWithPassword(user: User, password: String): Future[User] = {
//    val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
//    val authInfo = passwordRegistry.current.hash(password)
//  }
}
