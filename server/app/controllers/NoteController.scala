package controllers

import com.mohiva.play.silhouette.api.Silhouette
import db.Pagination
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.NoteService
import services.NoteService._
import utils.auth.CookieAuthEnv

import scala.concurrent.ExecutionContext

class NoteController(cc: ControllerComponents,
                     silhouette: Silhouette[CookieAuthEnv],
                     noteService: NoteService)
                    (implicit ec: ExecutionContext) extends AbstractController(cc) {
  def getDiary(page: Int) = silhouette.SecuredAction async { implicit request =>
    noteService
      .loadDiary(request.identity.id, Pagination(page, perPage = 10))
      .map(s => Ok(Json.toJson(s)))
  }

  def toggleHeart(noteId: Long) = silhouette.SecuredAction async { implicit request =>
    noteService.toggleHeart(request.identity.id, noteId).map {
      case Some(result) => Ok(Json.toJson(result))
      case _ => UnprocessableEntity
    }
  }
}
