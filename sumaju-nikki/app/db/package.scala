import java.io.Closeable
import java.time.LocalDateTime
import javax.sql.DataSource

import akka.actor.ActorSystem
import io.getquill.{NamingStrategy, PostgresJdbcContext, SnakeCase}
import play.api.libs.concurrent.CustomExecutionContext

package object db {
  type DbCtx = ExtendedPostgresJdbcContext[SnakeCase]

  class ExtendedPostgresJdbcContext[N <: NamingStrategy](naming: N, dataSource: DataSource with Closeable)
    extends PostgresJdbcContext(naming, dataSource) {

    implicit class LocalDateTimeQuotes(lhs: LocalDateTime) {
      def >(rhs: LocalDateTime) = quote(infix"$lhs > $rhs".as[Boolean])
      def <(rhs: LocalDateTime) = quote(infix"$lhs < $rhs".as[Boolean])
      def >=(rhs: LocalDateTime) = quote(infix"$lhs >= $rhs".as[Boolean])
      def <=(rhs: LocalDateTime) = quote(infix"$lhs <= $rhs".as[Boolean])
    }

    implicit class ForUpdate[T](q: Query[T]) {
      def forUpdate = quote(infix"$q FOR UPDATE".as[Query[T]])
    }

    implicit class RandomSort[T](q: Query[T]) {
      def randomSort = quote(infix"$q ORDER BY random()".as[Query[T]])
    }
  }

  class DbExecutionContext(system: ActorSystem, name: String)
    extends CustomExecutionContext(system, name)
}
