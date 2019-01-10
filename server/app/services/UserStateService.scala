package services

import models.{Student, StudentDao}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContext, Future}

class UserStateService(val studentDao: StudentDao)(implicit ec: ExecutionContext) {
  case class UserState(student: Student)
  implicit val userWrites = Json.writes[Student]
  implicit val userStateWrites = Json.writes[UserState]

  def getForUser(userId: Long): Future[Option[UserState]] = Future {
    for {
      student <- studentDao.findForUser(userId)
    }
    yield UserState(student)
  }
}
