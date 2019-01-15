import java.io.Closeable
import java.time.LocalDateTime

import javax.sql.DataSource
import akka.actor.ActorSystem
import enumeratum.{Enum, EnumEntry}
import io.getquill._
import play.api.libs.concurrent.CustomExecutionContext

package object db {
  type DbCtx = ExtendedPostgresJdbcContext[CompositeNamingStrategy2[SnakeCase, PluralizedTableNames]]
  type PgEnumRaw = String

  trait PgEnum[E <: EnumEntry] { this: Enum[E] =>
    implicit val enumDecoder: MappedEncoding[PgEnumRaw, E] =  MappedEncoding(this.withName)
    implicit val enumEncoder: MappedEncoding[E, PgEnumRaw] = MappedEncoding(_.entryName)
  }

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
      def takeRandom = quote(infix"$q ORDER BY random() LIMIT 1".as[Query[T]])
    }

    implicit val pgEnumRawEncoder: Encoder[PgEnumRaw] = encoder(java.sql.Types.OTHER,
      (index, value, row) => row.setObject(index, value, java.sql.Types.OTHER))

    implicit val pgEnumRawDecoder: Decoder[PgEnumRaw] = decoder((index, row) =>
      row.getObject(index).toString)
  }

  class DbExecutionContext(system: ActorSystem, name: String)
    extends CustomExecutionContext(system, name)
}
