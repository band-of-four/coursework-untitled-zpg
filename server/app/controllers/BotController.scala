package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.SuggestionService
import utils.auth.BotSecuredAction
import models.{CreatureForApproval, NoteForApproval}

import scala.concurrent.{ExecutionContext, Future}

class BotController(cc: ControllerComponents,
                    botSecuredAction: BotSecuredAction,
                    suggestionService: SuggestionService)
                   (implicit ec: ExecutionContext) extends AbstractController(cc) {
  case class BotRequest(id: Long)
  implicit val requestReads = Json.reads[BotRequest]
  implicit val noteWrites = Json.writes[NoteForApproval]
  implicit val creatureWrites = Json.writes[CreatureForApproval]

  def getUnapproved() = botSecuredAction async { request =>
    suggestionService.getFirstUnapprovedCreature().map {
      case Some(c) => Ok(Json.toJson(c))
      case None => NotFound
    }
  }

  def postApproved() = botSecuredAction(parse.json) async { request =>
    request.body.validate[BotRequest].fold(
      err => Future.successful(UnprocessableEntity),
      data => Future.successful(Ok(Json.obj("id" -> data.id)))
    )
  }
}
