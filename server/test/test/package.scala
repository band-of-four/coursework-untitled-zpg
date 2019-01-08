import java.io.File

import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import org.scalatestplus.play.{FakeApplicationFactory, PlaySpec}
import play.api.{Application, ApplicationLoader, Configuration, Environment}
import play.api.inject.DefaultApplicationLifecycle
import play.core.DefaultWebCommands

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration._

package object test {
  trait AppFactory extends FakeApplicationFactory {
    def buildContext(): ApplicationLoader.Context = {
      val env = Environment.simple(new File("."))
      ApplicationLoader.Context(
        environment = env,
        sourceMapper = None,
        webCommands = new DefaultWebCommands(),
        initialConfiguration = Configuration(ConfigFactory.load("application.test.conf")),
        lifecycle = new DefaultApplicationLifecycle()
      )
    }

    override def fakeApplication: Application =
      new AppLoader().load(buildContext())
  }

  trait DataSpec extends PlaySpec with OneAppPerSuiteWithComponents with AppFactory with BeforeAndAfterAll {
    override lazy val context: ApplicationLoader.Context = buildContext()

    override def components = new AppComponents(context)

    def await[T](awaitable: Awaitable[T]): T =
      Await.result(awaitable, 10 minutes)

    override protected def beforeAll(): Unit = {
      val conn = components.dbApi.database("default").getConnection()
      conn.createStatement().executeUpdate("""
        DELETE FROM user_oauth2_infos; DELETE FROM user_password_infos; DELETE FROM user_login_infos; DELETE FROM users;
      """)
    }
  }
}
