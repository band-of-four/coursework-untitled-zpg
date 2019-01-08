package actors

import akka.actor.Actor
import actors.GameProgressionActor.{MaxConcurrentUpdates, DelayWhenNoUpdatesPending, Poll}
import services.GameProgressionService

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object GameProgressionActor {
  case object Poll

  private val MaxConcurrentUpdates = 10
  private val DelayWhenNoUpdatesPending = 1 second
}

class GameProgressionActor(gameProgressionService: GameProgressionService) extends Actor {
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive = {
    case Poll =>
      gameProgressionService.pendingUpdates(MaxConcurrentUpdates) match {
        case Nil =>
          context.system.scheduler.scheduleOnce(DelayWhenNoUpdatesPending, self, Poll)
        case updates =>
          Await.ready(Future.traverse(updates)(u => Future(gameProgressionService.performUpdate(u))), Duration.Inf)
          self ! Poll
      }
  }
}
