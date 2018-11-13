package services

import org.scalatest.Matchers._
import com.mohiva.play.silhouette.api.LoginInfo
import models.{User, UserLoginInfo}

class UserServiceTest extends test.DataSpec {
  val service = components.userService

  "UserService" should {
    "create a new user with password" in {
      val user = await(service.saveWithPassword(User("peridot@homeworld"), "yellow"))
      user.email shouldBe "peridot@homeworld"

      val loginInfo = await(components.userLoginInfoDao.findByUserId(user.id)).orNull
      loginInfo should not be null
      loginInfo.providerKey shouldBe "peridot@homeworld"
    }

    // TODO: test transactions (create a logininfo to raise a uniqueness constraint violation)

    "find a user by their LoginInfo" in {
      val uDao = components.userDao
      val liDao = components.userLoginInfoDao

      val user = await(uDao.create(User("h")))

      await(liDao.findUserIdByLoginInfo(LoginInfo("id", "key"))) shouldBe None
      await(liDao.create(UserLoginInfo(user.id, "id", "key"))) should not be None

      val fetched = await(liDao.findByLoginInfo(LoginInfo("id", "key"))).orNull
      fetched should not be null
      fetched.providerId shouldBe "id"
      fetched.providerKey shouldBe "key"
    }
  }
}
