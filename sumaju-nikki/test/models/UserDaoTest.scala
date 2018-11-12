import org.scalatest.Matchers._
import com.mohiva.play.silhouette.api.LoginInfo
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import models.LoginInfos

class UserDaoTest extends PlaySpec with OneAppPerSuiteWithComponents with test.AppFactory {
  override def components = new AppComponents(context)

  "UserDao" should {
    "find a user by their LoginInfo" in {
      val loginInfos = new LoginInfos(components.db)

      var res = loginInfos.find(new LoginInfo("id", "key"))
      res shouldBe None

      loginInfos.create(new LoginInfo("id", "key"))
      res = loginInfos.find(new LoginInfo("id", "key"))
      res.map(_.providerID) shouldBe Some("id")
      res.map(_.providerKey) shouldBe Some("key")
    }
  }
}
