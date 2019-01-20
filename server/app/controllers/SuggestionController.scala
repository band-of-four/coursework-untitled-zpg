package controllers

import com.mohiva.play.silhouette.api.Silhouette
import db.Pagination
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.SuggestionService
import services.SuggestionService._
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
      err => Future.successful(BadRequest),
      suggestion => suggestionService.create(request.identity.id, suggestion).map(_ => Ok)
    )
  }

  def createCreature() = silhouette.SecuredAction(parse.json) async { request =>
    request.body.validate[SuggestionService.CreatureSuggestion].fold(
      err => Future.successful(BadRequest),
      suggestion => suggestionService.create(request.identity.id, suggestion).map(_ => Ok)
    )
  }

  def getApproved(page: Int) = silhouette.SecuredAction async { request =>
    suggestionService
      .getCreatedByUser(request.identity.id, Pagination(page, perPage = 10))
      .map(s => Ok(Json.toJson(s)))
  }
}
