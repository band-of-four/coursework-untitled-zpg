package services

import java.time.LocalDateTime

import game.Travel.TravelDuration
import models.Student.Gender._
import models.Student.StageTravel
import models.{Student, StudentDao}
import org.postgresql.util.PSQLException
import services.StudentService.{NewStudent, StudentAlreadyExistsException}

import scala.concurrent.{ExecutionContext, Future}

object StudentService {
  case class NewStudent(name: String, gender: Gender)

  class StudentAlreadyExistsException extends RuntimeException
}

class StudentService(val studentDao: StudentDao)(implicit ec: ExecutionContext) {
  def get(userId: Long): Future[Option[Student]] = Future {
    studentDao.findForUser(userId)
  }

  def create(userId: Long, entry: NewStudent): Future[Student] = Future {
    studentDao.create(Student(
      entry.name,
      entry.gender,
      level = 0,
      hp = 100,
      currentRoom = 1,
      stage = StageTravel,
      nextStageTime = LocalDateTime.now().plus(TravelDuration),
      id = userId
    ))
  } recoverWith {
    case e: PSQLException =>
      if (e.getMessage.startsWith("ERROR: duplicate key value"))
        Future.failed(new StudentAlreadyExistsException())
      else
        Future.failed(e)
  }
}
