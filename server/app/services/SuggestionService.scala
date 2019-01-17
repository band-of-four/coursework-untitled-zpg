package services

import game.Fight
import models._
import play.api.libs.json.Json
import services.SuggestionService._

import scala.concurrent.{ExecutionContext, Future}

object SuggestionService {
  case class TextSuggestion(text: String, gender: Student.Gender, lessonName: Option[String])
  case class CreatureSuggestion(name: String, notes: Seq[CreatureTextSuggestion])
  case class CreatureTextSuggestion(text: String, gender: Student.Gender, stage: Student.Stage)

  implicit val textSuggestionReads = Json.reads[TextSuggestion]
  implicit val creatureTextSuggestionReads = Json.reads[CreatureTextSuggestion]
  implicit val creatureSuggestionReads = Json.reads[CreatureSuggestion]
}

class SuggestionService(noteDao: NoteDao,
                        studentDao: StudentDao,
                        creatureDao: CreatureDao,
                        lessonDao: LessonDao)
                       (implicit ec: ExecutionContext) {
  def create(creatorId: Long, suggestion: TextSuggestion): Future[Unit] = Future {
    suggestion match {
      case TextSuggestion(text, gender, Some(lessonName)) =>
        noteDao.createForLesson(creatorId, text, gender, lessonId = lessonDao.findIdByName(lessonName))
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

  def getLessonNamesForStudent(studentId: Long): Future[Seq[String]] = Future {
    lessonDao.findNamesForStudentId(studentId)
  }
}
