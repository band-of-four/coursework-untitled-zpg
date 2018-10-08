import java.io.File

import org.scalatestplus.play.FakeApplicationFactory
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
}
