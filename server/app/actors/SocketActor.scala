package actors

import actors.UserSocketMapActor.{RegisterConnection, UnregisterConnection}
import actors.SocketActor.{InboundGetStage, OutboundStageUpdate}
import akka.actor.{Actor, ActorRef, Props}
import play.api.libs.json.Json
import services.StageService
import services.StageService._

object SocketActor {
  def props(userId: Long, userSocketMap: ActorRef, stageService: StageService)(out: ActorRef) =
    Props(new SocketActor(userId, out, userSocketMap, stageService))

  val InboundGetStage = "GetStage"
  val OutboundStageUpdate = "StageUpdate"
}

class SocketActor(userId: Long,
                  out: ActorRef,
                  userSocketMap: ActorRef,
                  stageService: StageService) extends Actor {
  override def preStart(): Unit =
    userSocketMap ! RegisterConnection(userId, self)

  override def postStop(): Unit =
    userSocketMap ! UnregisterConnection(userId, self)

  def receive = {
    case InboundGetStage =>
      val response = stageService.getCurrentStage(userId)
      out ! Json.obj("type" -> OutboundStageUpdate, "payload" -> Json.toJson(response)).toString
    case msg: String =>
      out ! s"$userId posted a message: $msg"
  }
}
