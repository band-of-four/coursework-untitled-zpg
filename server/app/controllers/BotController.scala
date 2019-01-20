package controllers

import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import services.SuggestionService
import utils.auth.BotSecuredAction

import scala.concurrent.{ExecutionContext, Future}

class BotController(cc: ControllerComponents,
                    suggestionService: SuggestionService)
                   (implicit ec: ExecutionContext) extends AbstractController(cc) {
  case class BotRequest(id: Long)
  implicit val requestReads = Json.reads[BotRequest]

  val botSecuredAction = new BotSecuredAction(cc)

  def get() = botSecuredAction async { request =>
    Future.successful(Ok)
  }

  def post() = botSecuredAction(parse.json) async { request =>
    request.body.validate[BotRequest].fold(
      err => Future.successful(UnprocessableEntity),
      data => Future.successful(Ok(Json.obj("id" -> data.id)))
    )
  }
}
