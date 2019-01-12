package services

import java.time.LocalDateTime

import game.Travel.TravelDuration
import models._
import org.postgresql.util.PSQLException
import services.StudentService.{NewStudent, StudentAlreadyExistsException, StudentSpell}

import scala.concurrent.{ExecutionContext, Future}

object StudentService {
  case class NewStudent(name: String, gender: Student.Gender)
  case class StudentSpell(name: String, kind: String, power: Int)

  class StudentAlreadyExistsException extends RuntimeException
}

class StudentService(studentDao: StudentDao,
                     spellDao: SpellDao,
                     studentDiaryDao: StudentDiaryDao)
                    (implicit ec: ExecutionContext) {
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
      stage = Student.Stage.Travel,
      stageStartTime = LocalDateTime.now(),
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

  def getSpells(userId: Long): Future[Seq[StudentSpell]] = Future {
    spellDao.findLearned(userId).map(spell =>
      StudentSpell(spell.name, spell.kind, spell.power))
  }

  def getDiaryNotes(userId: Long): Future[Seq[StudentDiaryNote]] = Future {
    studentDiaryDao.load(userId, 10)
  }
}
