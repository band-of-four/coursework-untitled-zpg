package controllers

import com.mohiva.play.silhouette.api.Silhouette
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.SuggestionService
import utils.auth.CookieAuthEnv

import scala.concurrent.{ExecutionContext, Future}

class SuggestionController(cc: ControllerComponents,
                           silhouette: Silhouette[CookieAuthEnv],
                           suggestionService: SuggestionService)
                          (implicit ec: ExecutionContext) extends AbstractController(cc) {
  def getLessonNames() = silhouette.SecuredAction async { request =>
    suggestionService.getLessonNamesForStudent(request.identity.id).map(ns => Ok(Json.toJson(ns)))
  }

  def createText() = silhouette.SecuredAction(parse.json) async { request =>
    request.body.validate[SuggestionService.TextSuggestion].fold(
      err =>
        Future.successful(BadRequest),
      suggestion =>
        suggestionService.create(request.identity.id, suggestion).map {
          case Right(_) => Ok
          case Left(err) => UnprocessableEntity(err)
        }
    )
  }
}
