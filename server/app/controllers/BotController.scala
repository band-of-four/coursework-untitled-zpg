package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.SuggestionService
import services.SuggestionService.{NoteApproved, CreatureApproved}
import utils.auth.BotSecuredAction
import models.{CreatureForApproval, NoteForApproval, NoteForApprovalNamed}

import scala.concurrent.{ExecutionContext, Future}


class BotController(cc: ControllerComponents,
                    botSecuredAction: BotSecuredAction,
                    suggestionService: SuggestionService)
                   (implicit ec: ExecutionContext) extends AbstractController(cc) {
  
  implicit val noteFormat = Json.format[NoteApproved]
  implicit val creatureFormat = Json.format[CreatureApproved]
  implicit val noteWrites = Json.writes[NoteForApproval]
  implicit val creatureWrites = Json.writes[CreatureForApproval]
  implicit val noteNamedWrites = Json.writes[NoteForApprovalNamed]

  def getUnapprovedCreature() = botSecuredAction async { request =>
    suggestionService.getFirstUnapprovedCreature().map {
      case Some(c) => Ok(Json.toJson(c))
      case None => NotFound
    }
  }

  def approveCreature() = botSecuredAction(parse.json) async { request =>
    request.body.validate[CreatureApproved].fold(
      err => Future.successful(UnprocessableEntity),
      data => {
        suggestionService.applyApprovedCreature(data)
        Future.successful(Ok)
      }
    )
  }

  def getUnapprovedNote() = botSecuredAction async { request =>
    suggestionService.getFirstUnapprovedNote().map {
      case Some(n) => Ok(Json.toJson(n))
      case None => NotFound
    }
  }

  def approveNote() = botSecuredAction(parse.json) async { request =>
    request.body.validate[NoteApproved].fold(
      err => Future.successful(UnprocessableEntity),
      data => {
        suggestionService.applyApprovedNote(data)
        Future.successful(Ok)
      }
    )
  }
}
