package services

import java.time.LocalDateTime

import db.Pagination
import game.Durations
import models._
import org.postgresql.util.PSQLException
import play.api.libs.json.Json
import services.StudentService._
import utils.Collections._

import scala.concurrent.{ExecutionContext, Future}

object StudentService {
  case class NewStudent(name: String, gender: Student.Gender)
  case class StudentSpell(name: String, kind: Spell.Kind, power: Int)

  case class DiarySection(heading: DiarySectionHeading, notes: Seq[DiarySectionNote])
  case class DiarySectionHeading(lesson: Option[String], club: Option[String], creature: Option[String],
                                 travel: Boolean, library: Boolean)
  case class DiarySectionNote(text: String, date: LocalDateTime, heartCount: Long, isHearted: Boolean)

  implicit val studentWrites = Json.writes[Student]
  implicit val spellWrites = Json.writes[StudentService.StudentSpell]
  implicit val entryReads = Json.reads[StudentService.NewStudent]

  implicit val noteWrites = Json.writes[DiarySectionNote]
  implicit val headingWrites = Json.writes[DiarySectionHeading]
  implicit val sectionWrites = Json.writes[DiarySection]

  class StudentAlreadyExistsException extends RuntimeException
}

class StudentService(studentDao: StudentDao,
                     spellDao: SpellDao,
                     noteDao: NoteDao,
                     studentDiaryDao: StudentDiaryDao)
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

  def getDiarySections(userId: Long): Future[Seq[DiarySection]] = Future {
    val notes = studentDiaryDao.load(userId, Pagination(page = 0, perPage = 10))

    notes
      .groupConsecutiveBy { note =>
        DiarySectionHeading(note.lesson, note.club, note.creature,
          travel = note.stage == Student.Stage.Travel,
          library = note.stage == Student.Stage.Library)
      }
      .map { case (heading, notes) =>
        DiarySection(heading, notes.map(n => DiarySectionNote(n.text, n.date, n.heartCount, n.isHearted)))
      }
  }
}
