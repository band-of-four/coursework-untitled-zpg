package actors

import actors.SocketMessengerActor.{MessageUserActors, RegisterConnection, UnregisterConnection}
import akka.actor.{Actor, ActorRef}

object SocketMessengerActor {
  case class RegisterConnection(userId: Long, actor: ActorRef)
  case class UnregisterConnection(userId: Long, actor: ActorRef)
  case class MessageUserActors(userId: Long, message: Any)
}

class SocketMessengerActor extends Actor {
  type UserSocketMap = Map[Long, List[ActorRef]]

  override def receive = tracking(Map())

  private def tracking(socketMap: UserSocketMap): Receive = {
    case RegisterConnection(userId, actor) =>
      val updatedMap = socketMap.get(userId) match {
        case Some(actors) =>
          socketMap + (userId -> (actor :: actors))
        case None =>
          socketMap + (userId -> List(actor))
      }
      context.become(tracking(updatedMap))
    case UnregisterConnection(userId, actor) =>
      val updatedMap = socketMap.get(userId) match {
        case Some(`actor` :: Nil) =>
          socketMap - userId
        case Some(actors) =>
          socketMap + (userId -> actors.filterNot(_ == actor))
        case _ =>
          socketMap
      }
      context.become(tracking(updatedMap))
    case MessageUserActors(userId, message) =>
      socketMap.get(userId) match {
        case Some(actors) =>
          actors.foreach(_ ! message)
        case _ =>
      }
  }
}
