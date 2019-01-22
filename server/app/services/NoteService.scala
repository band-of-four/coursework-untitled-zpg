package services

import java.time.LocalDateTime

import play.api.libs.json.Json
import db.Pagination
import models._
import services.NoteService._
import utils.Collections._

import scala.concurrent.{ExecutionContext, Future}

object NoteService {
  case class DiarySection(heading: DiarySectionHeading, notes: Seq[DiarySectionNote])

  case class DiarySectionHeading(date: LocalDateTime,
                                 lesson: Option[String], club: Option[String], creature: Option[String],
                                 travel: Boolean, infirmary: Boolean, library: Boolean) {
    override def equals(o: Any) = o match {
      case DiarySectionHeading(_, `lesson`, `club`, `creature`, `travel`, `infirmary`, `library`) => true
      case _ => false
    }

    override def hashCode = (lesson, club, creature, travel, infirmary).##
  }

  case class DiarySectionNote(id: Long, text: String, heartCount: Long, isHearted: Boolean)

  case class FormattedNote(id: Long, text: String, stage: Student.Stage,
                           lesson: Option[String], club: Option[String], creature: Option[String],
                           heartCount: Long, isHearted: Boolean)

  implicit val noteWrites = Json.writes[DiarySectionNote]
  implicit val headingWrites = Json.writes[DiarySectionHeading]
  implicit val sectionWrites = Json.writes[DiarySection]
  implicit val formattedWrites = Json.writes[FormattedNote]
  implicit val toggledWrites = Json.writes[NoteHeartToggled]
}

class NoteService(noteDao: NoteDao,
                  diaryDao: StudentDiaryDao,
                  libraryVisitDao: StudentLibraryVisitDao)
                 (implicit ec: ExecutionContext) {
  def loadStageNote(student: Student): FormattedNote =
    formatNote(student.id, noteDao.load(student.id, student.stageNoteId))

  def toggleHeart(userId: Long, noteId: Long): Future[Option[NoteHeartToggled]] = Future {
    noteDao.toggleHeart(userId, noteId)
  }

  def loadDiary(userId: Long, pagination: Pagination): Future[Seq[DiarySection]] = Future {
    val noteList = diaryDao.load(userId, pagination)

    noteList
      .groupConsecutiveBy { note =>
        DiarySectionHeading(note.date, note.lesson, note.club, note.creature,
          travel = note.stage == Student.Stage.Travel,
          infirmary = note.stage == Student.Stage.Infirmary,
          library = note.stage == Student.Stage.Library)
      }
      .map { case (heading, notes) =>
        DiarySection(heading, notes.map(n => DiarySectionNote(n.id, n.text, n.heartCount, n.isHearted)))
      }
  }

  def formatNote(studentId: Long, note: NotePreloaded): FormattedNote = note match {
    case NotePreloaded(id, text, Student.Stage.Library, _, _, _, heartCount, isHearted) =>
      val acquiringSpell = libraryVisitDao.load(studentId)
      val formatted = text
        .replace("{spell_name}", acquiringSpell.spellName)
        .replace("{spell_modifier}", acquiringSpell.spellPower.toString)
      FormattedNote(note.id, formatted, note.stage, note.lesson, note.club, note.creature, note.heartCount, note.isHearted)
    case _ =>
      FormattedNote(note.id, note.text, note.stage, note.lesson, note.club, note.creature, note.heartCount, note.isHearted)
  }
}
