package services

import org.scalatest.Matchers._
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.{User, UserLoginInfo}

class UserServiceTest extends test.DataSpec {
  private val service = components.userService

  "UserService" should {
    "create a new user with password" in {
      val user = await(service.saveEmail("peridot@homeworld", "yellow"))
      user.email shouldBe Some("peridot@homeworld")

      val loginInfo = components.userLoginInfoDao.findByUserId(user.id).orNull
      loginInfo should not be null
      loginInfo.providerKey shouldBe "peridot@homeworld"
    }

    "creates a user transactionally" in {
      val user = components.userDao.create(User(Some("firstcome@firstserve")))
      val existingLoginInfo = components.userLoginInfoDao.create(
        UserLoginInfo(user.id, CredentialsProvider.ID, "newcomer@wired"))

      an [UserService.EmailAlreadyTakenException] should be thrownBy
        await(service.saveEmail("newcomer@wired", "presentdaypresenttime"))

      components.userDao.findByEmail("newcomer@wired") shouldBe None
    }

    "find a user by their LoginInfo" in {
      val liDao = components.userLoginInfoDao

      val user = components.userDao.create(User(Some("h")))

      liDao.findUserIdByLoginInfo(LoginInfo("id", "key")) shouldBe None
      liDao.create(UserLoginInfo(user.id, "id", "key")) should not be None

      val fetched = liDao.findByLoginInfo(LoginInfo("id", "key")).orNull
      fetched should not be null
      fetched.providerId shouldBe "id"
      fetched.providerKey shouldBe "key"
    }
  }
}
