package models

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import scala.concurrent.{ExecutionContext, Future}
import db.DbCtx
import utils.FutureOption

case class User(id: Long, email: String) extends Identity

class UserDao(val db: DbCtx, implicit val ec: ExecutionContext) {
  import db._

  private val schema = quote(querySchema[User]("users"))

  def create(user: User): Future[User] = Future {
    user.copy(
      id = run(schema.insert(lift(user)).returning(_.id))
    )
  }

  def findById(id: Long): FutureOption[User] = Future {
    run(schema.filter(_.id == lift(id))).headOption
  }
}
