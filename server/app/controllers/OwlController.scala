package controllers

import com.mohiva.play.silhouette.api.Silhouette
import models.OwlPreloaded
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.OwlService
import services.OwlService._
import utils.auth.CookieAuthEnv

import scala.concurrent.ExecutionContext

class OwlController(cc: ControllerComponents,
                    silhouette: Silhouette[CookieAuthEnv],
                    owlService: OwlService)
                   (implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val owlWrites = Json.writes[OwlPreloaded]

  def get() = silhouette.SecuredAction async { request =>
    owlService.getSorted(request.identity.id).map(r => Ok(Json.toJson(r)))
  }

  def apply(owlId: Long) = silhouette.SecuredAction(parse.json) async { request =>
    owlService.apply(request.identity.id, owlId, request.body) map {
      case ImmediateApplied(message) => Ok(Json.obj("status" -> "applied", "message" -> message))
      case ImmediateFailed(message) => UnprocessableEntity(Json.obj("status" -> "failed", "message" -> message))
      case NonImmediateApplied => Ok(Json.obj("status" -> "applied"))
      case NotApplicable => UnprocessableEntity(Json.obj("status" -> "failed"))
    }
  }
}
