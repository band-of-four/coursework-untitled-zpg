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
  
  case class ApprovedNote(id: Long, text: String, is_approved: Boolean)
  case class ApprovedCreature(id: Long, name: String, is_approved: Boolean, notes: Seq[ApprovedNote])
  implicit val noteFormat = Json.format[ApprovedNote]
  implicit val creatureFormat = Json.format[ApprovedCreature]
  implicit val noteWrites = Json.writes[NoteForApproval]
  implicit val creatureWrites = Json.writes[CreatureForApproval]

  def getUnapprovedCreature() = botSecuredAction async { request =>
    suggestionService.getFirstUnapprovedCreature().map {
      case Some(c) => Ok(Json.toJson(c))
      case None => NotFound
    }
  }

  def approveCreature() = botSecuredAction(parse.json) async { request =>
    request.body.validate[ApprovedCreature].fold(
      err => Future.successful(UnprocessableEntity),
      data => {
        Console.print(data)
        Future.successful(Ok)
      }
    )
  }

  //def getUnapprovedNote() {}

  //def approveNote() {}
}
