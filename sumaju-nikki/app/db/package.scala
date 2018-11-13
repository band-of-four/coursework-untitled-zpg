import akka.actor.ActorSystem
import io.getquill.{PostgresJdbcContext, SnakeCase}
import play.api.libs.concurrent.CustomExecutionContext

package object db {
  type DbCtx = PostgresJdbcContext[SnakeCase]

  class DbExecutionContext(system: ActorSystem, name: String)
    extends CustomExecutionContext(system, name)
}
