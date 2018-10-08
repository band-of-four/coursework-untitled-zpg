import io.getquill.{PostgresJdbcContext, NamingStrategy, SnakeCase}

package object db {
  type DbCtx = PostgresJdbcContext[SnakeCase]
}
