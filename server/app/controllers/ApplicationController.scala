package controllers

import actors.SocketActor
import akka.actor.{ActorRef, ActorSystem}
import akka.stream.Materializer
import com.mohiva.play.silhouette.api.{HandlerResult, Silhouette}
import play.api.libs.streams.ActorFlow
import play.api.mvc._
import utils.auth.CookieAuthEnv

import scala.concurrent.{ExecutionContext, Future}

class ApplicationController(cc: ControllerComponents,
                            silhouette: Silhouette[CookieAuthEnv],
                            userSocketMap: ActorRef)
                           (implicit mat: Materializer,
                            ec: ExecutionContext,
                            actorSystem: ActorSystem) extends AbstractController(cc) {
  def index() = Action { implicit request =>
    Ok(views.html.index())
  }

  def connect() = WebSocket acceptOrResult[String, String] { request =>
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
