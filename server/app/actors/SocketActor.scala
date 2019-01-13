package actors

import actors.SocketMessengerActor.{RegisterConnection, UnregisterConnection}
import actors.SocketActor.{InboundGetStage, OutboundStageUpdate, PushStageUpdate}
import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.Json
import services.StageService
import services.StageService._

object SocketActor {
  def props(userId: Long, socketMessenger: ActorRef, stageService: StageService)(out: ActorRef) =
    Props(new SocketActor(userId, out, socketMessenger, stageService))

  val InboundGetStage = "GetStage"
  val OutboundStageUpdate = "StageUpdate"

  case class PushStageUpdate(update: StageUpdate)
}

class SocketActor(userId: Long,
                  out: ActorRef,
                  socketMessenger: ActorRef,
                  stageService: StageService) extends Actor {
  override def preStart(): Unit =
    socketMessenger ! RegisterConnection(userId, self)

  override def postStop(): Unit =
    socketMessenger ! UnregisterConnection(userId, self)

  def receive = {
    case PushStageUpdate(update) =>
      out ! serializeUpdate(update)
    case InboundGetStage =>
      out ! serializeUpdate(stageService.getStage(userId))
  }

  def serializeUpdate(update: StageUpdate) =
    Json.obj("type" -> OutboundStageUpdate, "payload" -> Json.toJson(update)).toString
}
