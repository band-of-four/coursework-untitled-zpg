import org.scalatest.Matchers._
import com.mohiva.play.silhouette.api.LoginInfo
import models.{User, UserLoginInfo}

import scala.concurrent.Await.result
import scala.concurrent.duration._

class UserDaoTest extends test.DataSpec {
  "UserDao" should {
    "find a user by their LoginInfo" in {
      val uDao = components.userDao
      val liDao = components.userLoginInfoDao

      val user = result(uDao.create(User(-1, "h")), 500 millis)

      result(liDao.findUserIdByLoginInfo(LoginInfo("id", "key")), 500 millis) shouldBe None
      result(liDao.create(UserLoginInfo(user.id, "id", "key")), 500 millis) should not be None

      val fetched = result(liDao.findByLoginInfo(LoginInfo("id", "key")), 500 millis).orNull
      fetched should not be null
      fetched.providerId shouldBe "id"
      fetched.providerKey shouldBe "key"
    }
  }
}
