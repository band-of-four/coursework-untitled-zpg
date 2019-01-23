package services

import db.Pagination
import game.Fight
import models._
import play.api.libs.json.Json
import services.SuggestionService._

import scala.concurrent.{ExecutionContext, Future}

object SuggestionService {
  case class NoteApproved(id: Long, text: String, isApproved: Boolean)
  case class CreatureApproved(id: Long, name: String, isApproved: Boolean, notes: Seq[NoteApproved])
  case class TextSuggestion(text: String, gender: Student.Gender, lessonName: Option[String], clubName: Option[String], stage: Option[Student.Stage])
  case class CreatureSuggestion(name: String, notes: Seq[CreatureTextSuggestion])
  case class CreatureTextSuggestion(text: String, gender: Student.Gender, stage: Student.Stage)

  implicit val textSuggestionReads = Json.reads[TextSuggestion]
  implicit val creatureTextSuggestionReads = Json.reads[CreatureTextSuggestion]
  implicit val creatureSuggestionReads = Json.reads[CreatureSuggestion]
  implicit val creatorNoteWrites = Json.writes[NoteForCreator]
}

class SuggestionService(noteDao: NoteDao,
                        studentDao: StudentDao,
                        creatureDao: CreatureDao,
                        clubDao: ClubDao,
                        lessonDao: LessonDao)
                       (implicit ec: ExecutionContext) {
  def create(creatorId: Long, suggestion: TextSuggestion): Future[Unit] = Future {
    suggestion match {
      case TextSuggestion(text, gender, Some(lessonName), _, _) =>
        noteDao.createForLesson(creatorId, text, gender, lessonId = lessonDao.findIdByName(lessonName))
      case TextSuggestion(text, gender, _, Some(clubName), _) =>
        noteDao.createForClub(creatorId, text, gender, clubId = clubDao.findIdByName(clubName))
      case TextSuggestion(text, gender, _, _, Some(stage)) =>
        noteDao.createForStage(creatorId, text, gender, stage)
    }
  }

  def create(creatorId: Long, suggestion: CreatureSuggestion): Future[Unit] = Future {
    val studentLevel = studentDao.findLevelById(creatorId)
    val creatureStats = Fight.BaseCreatureStats(studentLevel)
    val creature = Creature(
      suggestion.name,
      totalHp = creatureStats.totalHp,
      power = creatureStats.power,
      level = studentLevel
    )
    creatureDao.create(creature) { creature =>
      suggestion.notes.foreach { note =>
        noteDao.createForCreature(creatorId, note.text, note.gender, note.stage, creature.id)
      }
    }
  }

  def getClubNamesForStudent(studentId: Long): Future[Seq[String]] = Future {
    clubDao.findNamesForStudentId(studentId)
  }

  def getLessonNamesForStudent(studentId: Long): Future[Seq[String]] = Future {
    lessonDao.findNamesForStudentId(studentId)
  }

  def getFirstUnapprovedCreature(): Future[Option[CreatureForApproval]] = Future {
    creatureDao.loadFirstUnapproved()
  }

  def getFirstUnapprovedNote(): Future[Option[NoteForApprovalNamed]] = Future {
    noteDao.loadFirstUnapproved()
  }

  def getCreatedByUser(userId: Long, pagination: Pagination): Future[Seq[NoteForCreator]] = Future {
    noteDao.loadForCreator(userId, pagination)
  }
  
  def applyApprovedCreature(ac: CreatureApproved): Future[Unit] = Future {
    creatureDao.applyApproved(ac)
  }

  def applyApprovedNote(an: NoteApproved): Future[Unit] = Future {
    noteDao.applyApproved(an)
  }
}
