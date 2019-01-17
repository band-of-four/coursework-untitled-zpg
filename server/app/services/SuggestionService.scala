package services

import models.{LessonDao, NoteDao, Student}
import play.api.libs.json.Json
import services.SuggestionService._

import scala.concurrent.{ExecutionContext, Future}

object SuggestionService {
  case class TextSuggestion(text: String, gender: Student.Gender, lessonName: Option[String])

  implicit val textSuggestionReads = Json.reads[TextSuggestion]

  val LessonNotFoundError = "lesson_not_found"
  val InvalidSuggestionFormatError = "invalid_suggestion_format"
}

class SuggestionService(noteDao: NoteDao,
                        lessonDao: LessonDao)
                       (implicit ec: ExecutionContext) {
  def create(creatorId: Long, suggestion: TextSuggestion): Future[Either[String, Unit]] = Future {
    suggestion match {
      case TextSuggestion(text, gender, Some(lessonName)) =>
        lessonDao.findIdByName(lessonName) match {
          case Some(lessonId) => Right(noteDao.createSuggestionForLesson(creatorId, text, gender, lessonId))
          case _ => Left(LessonNotFoundError)
        }
      case _ => Left(InvalidSuggestionFormatError)
    }
  }

  def getLessonNamesForStudent(studentId: Long): Future[Seq[String]] = Future {
    lessonDao.findNamesForStudentId(studentId)
  }
}
