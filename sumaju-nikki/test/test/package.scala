import java.io.File

import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.components.OneAppPerSuiteWithComponents
import org.scalatestplus.play.{FakeApplicationFactory, PlaySpec}
import play.api.{Application, ApplicationLoader, Configuration, Environment}
import play.api.inject.DefaultApplicationLifecycle
import play.core.DefaultWebCommands

package object test {
  trait AppFactory extends FakeApplicationFactory {
    override def fakeApplication: Application = {
      val env = Environment.simple(new File("."))
      val ctx = ApplicationLoader.Context(
        environment = env,
        sourceMapper = None,
        webCommands = new DefaultWebCommands(),
        initialConfiguration = Configuration.load(env),
        lifecycle = new DefaultApplicationLifecycle()
      )
      new AppLoader().load(ctx)
    }
  }

  trait DataSpec extends PlaySpec with OneAppPerSuiteWithComponents with AppFactory with BeforeAndAfterAll {
    override def components = new AppComponents(context)

    override protected def beforeAll(): Unit = {
      val conn = components.dbApi.database("default").getConnection()
      conn.createStatement().executeUpdate("DELETE FROM login_info; DELETE FROM users_login_info; DELETE FROM users;")
    }
  }
}
