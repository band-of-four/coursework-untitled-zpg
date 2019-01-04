package actors

import akka.actor.Actor
import actors.GameProgressionActor.{MAX_CONCURRENT_UPDATES, DELAY_WHEN_NO_PENDING_UPDATES, Poll}
import services.GameProgressionService

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration._

object GameProgressionActor {
  case object Poll

  private val MAX_CONCURRENT_UPDATES = 10
  private val DELAY_WHEN_NO_PENDING_UPDATES = 1 second
}

class GameProgressionActor(val gameProgressionService: GameProgressionService) extends Actor {
  implicit val executionContext: ExecutionContext = context.dispatcher

  override def receive = {
    case Poll =>
      gameProgressionService.pendingUpdates(MAX_CONCURRENT_UPDATES) match {
        case Nil =>
          context.system.scheduler.scheduleOnce(DELAY_WHEN_NO_PENDING_UPDATES, self, Poll)
        case updates =>
          Await.ready(Future.traverse(updates)(gameProgressionService.performUpdate), Duration.Inf)
          self ! Poll
      }
  }
}
