import org.scalatest.Matchers._
import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Await.result
import scala.concurrent.duration._

class UserDaoTest extends test.DataSpec {
  "UserDao" should {
    "find a user by their LoginInfo" in {
      val liDao = components.loginInfos

      result(liDao.find(new LoginInfo("id", "key")), 500 millis) shouldBe None
      result(liDao.create(new LoginInfo("id", "key")), 500 millis) should not be None

      val fetched = result(liDao.find(new LoginInfo("id", "key")), 500 millis).orNull
      fetched should not be null
      fetched.providerID shouldBe "id"
      fetched.providerKey shouldBe "key"
    }
  }
}
