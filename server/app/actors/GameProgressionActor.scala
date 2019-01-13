package actors

import akka.actor.{Actor, ActorRef}
import actors.GameProgressionActor.{DelayWhenNoUpdatesPending, MaxConcurrentUpdates, Poll}
import actors.SocketActor.PushStageUpdate
import actors.SocketMessengerActor.MessageUserActors
import models.StudentForUpdate
import services.GameProgressionService

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object GameProgressionActor {
  case object Poll

  private val MaxConcurrentUpdates = 10
  private val DelayWhenNoUpdatesPending = 1 second
}

class GameProgressionActor(gameProgressionService: GameProgressionService,
                           socketMessenger: ActorRef) extends Actor {
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive = {
    case Poll =>
      gameProgressionService.pendingUpdates(MaxConcurrentUpdates) match {
        case Nil =>
          context.system.scheduler.scheduleOnce(DelayWhenNoUpdatesPending, self, Poll)
        case updates =>
          Await.ready(Future.traverse(updates)(performUpdate), Duration.Inf)
          self ! Poll
      }
  }

  def performUpdate(student: StudentForUpdate): Future[Unit] = Future {
    val stageUpdate = gameProgressionService.updateStage(student)
    socketMessenger ! MessageUserActors(student.id, PushStageUpdate(stageUpdate))
  }
}
