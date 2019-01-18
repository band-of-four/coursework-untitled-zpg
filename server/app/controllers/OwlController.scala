package controllers

import com.mohiva.play.silhouette.api.Silhouette
import models.OwlPreloaded
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.OwlService
import utils.auth.CookieAuthEnv

import scala.concurrent.ExecutionContext

class OwlController(cc: ControllerComponents,
                    silhouette: Silhouette[CookieAuthEnv],
                    owlService: OwlService)
                   (implicit ec: ExecutionContext) extends AbstractController(cc) {
  implicit val owlWrites = Json.writes[OwlPreloaded]

  def get() = silhouette.SecuredAction async { implicit request =>
    owlService.getSorted(request.identity.id).map(r => Ok(Json.toJson(r)))
  }
}
