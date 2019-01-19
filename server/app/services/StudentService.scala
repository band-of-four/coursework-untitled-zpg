package services

import java.time.LocalDateTime

import db.Pagination
import game.Durations
import models._
import org.postgresql.util.PSQLException
import play.api.libs.json.Json
import services.StudentService._

import scala.concurrent.{ExecutionContext, Future}

object StudentService {
  case class NewStudent(name: String, gender: Student.Gender)
  case class StudentSpell(name: String, kind: Spell.Kind, power: Int)

  implicit val entryReads = Json.reads[StudentService.NewStudent]

  implicit val studentWrites = Json.writes[Student]
  implicit val spellWrites = Json.writes[StudentService.StudentSpell]
  implicit val relationshipWrites = Json.writes[StudentRelationshipPreloaded]

  class StudentAlreadyExistsException extends RuntimeException
}

class StudentService(studentDao: StudentDao,
                     spellDao: SpellDao,
                     noteDao: NoteDao,
                     relationshipDao: StudentRelationshipDao)
                    (implicit ec: ExecutionContext) {
  def get(userId: Long): Future[Option[Student]] = Future {
    studentDao.findForUser(userId)
  }

  def create(userId: Long, entry: NewStudent): Future[Student] = Future {
    val newStudent = Student(
      userId,
      entry.name,
      entry.gender,
      level = 0,
      hp = 100,
      currentRoom = 1,
      stageNoteId = noteDao.initialNoteId(entry.gender),
      stageStartTime = LocalDateTime.now(),
      nextStageTime = LocalDateTime.now().plus(Durations.Travel)
    )
    studentDao.create(newStudent) { student =>
      spellDao.createBaseSpells(student.id)
    }
  } recoverWith {
    case e: PSQLException =>
      if (e.getMessage.startsWith("ERROR: duplicate key value"))
        Future.failed(new StudentAlreadyExistsException())
      else
        Future.failed(e)
  }

  def getSpells(userId: Long): Future[Seq[SpellPreloaded]] = Future {
    spellDao.load(userId)
  }

  def getRelationships(userId: Long, pagination: Pagination): Future[Seq[StudentRelationshipPreloaded]] = Future {
    relationshipDao.loadForStudent(userId, pagination)
  }
}
