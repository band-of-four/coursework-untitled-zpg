package controllers

import com.mohiva.play.silhouette.api.Silhouette
import db.Pagination
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.StudentService
import services.StudentService._
import utils.auth.CookieAuthEnv

import scala.concurrent.{ExecutionContext, Future}

object StudentController {
  val AlreadyExists = "already_exists"
}

class StudentController(cc: ControllerComponents,
                        silhouette: Silhouette[CookieAuthEnv],
                        studentService: StudentService)
                       (implicit ec: ExecutionContext) extends AbstractController(cc) {
  def get() = silhouette.SecuredAction async { request =>
    studentService get request.identity.id map {
      case Some(student) => Ok(Json.toJson(student))
      case None => NotFound
    }
  }

  def create() = silhouette.SecuredAction(parse.json) async { request =>
    request.body.validate[StudentService.NewStudent].fold(
      err => Future.successful(BadRequest),
      entry => studentService.create(request.identity.id, entry).map(s => Ok(Json.toJson(s)))
    ).recover {
      case _: StudentAlreadyExistsException => UnprocessableEntity(StudentController.AlreadyExists)
    }
  }
  
  def getSpells() = silhouette.SecuredAction async { request =>
    studentService.getSpells(request.identity.id).map(s => Ok(Json.toJson(s)))
  }

  def getRelationships(page: Int) = silhouette.SecuredAction async { request =>
    studentService
      .getRelationships(request.identity.id, Pagination(page, perPage = 10))
      .map(s => Ok(Json.toJson(s)))
  }

  def getSkills(page: Int) = silhouette.SecuredAction async { request =>
    studentService
      .getSkills(request.identity.id, Pagination(page, perPage = 10))
      .map(s => Ok(Json.toJson(s)))
  }
}
