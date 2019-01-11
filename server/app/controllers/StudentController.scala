package controllers

import com.mohiva.play.silhouette.api.Silhouette
import models.{Student, StudentDao}
import play.api.libs.json.{Json, Reads}
import play.api.mvc.{AbstractController, ControllerComponents}
import services.StudentService
import services.StudentService.StudentAlreadyExistsException
import utils.auth.CookieAuthEnv

import scala.concurrent.{ExecutionContext, Future}

object StudentController {
  val AlreadyExists = "already_exists"
}

class StudentController(cc: ControllerComponents,
                        silhouette: Silhouette[CookieAuthEnv],
                        studentService: StudentService)
                       (implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val studentGenderReads = Reads.enumNameReads(Student.Gender)
  implicit val studentWrites = Json.writes[Student]
  implicit val spellWrites = Json.writes[StudentService.StudentSpell]
  implicit val entryReads = Json.reads[StudentService.NewStudent]

  def get() = silhouette.SecuredAction async { implicit request =>
    studentService get request.identity.id map {
      case Some(student) => Ok(Json.toJson(student))
      case None => NotFound
    }
  }

  def create() = silhouette.SecuredAction(parse.json) async { implicit request =>
    request.body.validate[StudentService.NewStudent].fold(
      err => Future.successful(BadRequest),
      entry => studentService.create(request.identity.id, entry).map(s => Ok(Json.toJson(s)))
    ).recover {
      case _: StudentAlreadyExistsException => UnprocessableEntity(StudentController.AlreadyExists)
    }
  }
  
  def getSpells() = silhouette.SecuredAction async { implicit request =>
    studentService getSpells request.identity.id map { s => Ok(Json.toJson(s)) }
  }
  
}
