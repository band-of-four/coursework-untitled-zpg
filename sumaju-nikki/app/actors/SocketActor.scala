package actors

import actors.UserSocketMapActor.{RegisterConnection, UnregisterConnection}
import akka.actor.{Actor, ActorRef, Props}

object SocketActor {
  def props(userId: Long, userSocketMap: ActorRef)(out: ActorRef) =
    Props(new SocketActor(userId, userSocketMap, out))
}

class SocketActor(userId: Long, userSocketMap: ActorRef, out: ActorRef) extends Actor {
  override def preStart(): Unit =
    userSocketMap ! RegisterConnection(userId, self)

  override def postStop(): Unit =
    userSocketMap ! UnregisterConnection(userId, self)

  def receive = {
    case msg: String =>
      out ! s"${userId} posted a message: $msg"
  }
}
