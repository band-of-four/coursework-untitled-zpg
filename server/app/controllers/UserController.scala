package controllers

import akka.actor.{ActorRef, ActorSystem}
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import actors.SocketActor
import akka.stream.Materializer
import models.StudentDao
import play.api.libs.json.Json
import services.UserStateService
import utils.auth.CookieAuthEnv

import scala.concurrent.{ExecutionContext, Future}

class UserController(cc: ControllerComponents,
                     silhouette: Silhouette[CookieAuthEnv],
                     userSocketMap: ActorRef,
                     userStateService: UserStateService)
                    (implicit mat: Materializer,
                     ec: ExecutionContext,
                     actorSystem: ActorSystem) extends AbstractController (cc) {

  def getState() = silhouette.SecuredAction async { implicit request =>
    import userStateService.userStateWrites

    userStateService.getForUser(request.identity.id).map {
      case Some(state) => Ok(Json.toJson(state))
      case None => NotFound
    }
  }

  def getSocket() = WebSocket acceptOrResult[String, String] { request =>
    implicit val req = Request(request, AnyContentAsEmpty)

    silhouette.SecuredRequestHandler { s =>
      Future.successful(HandlerResult(Ok, Some(s.identity)))
    } map {
      case HandlerResult(r, Some(user)) =>
        Right(ActorFlow.actorRef(SocketActor.props(user.id, userSocketMap)))
      case HandlerResult(r, None) =>
        Left(r)
    }
  }
}
