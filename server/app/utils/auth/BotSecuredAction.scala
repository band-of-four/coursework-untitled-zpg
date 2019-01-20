package utils.auth

import play.api.Configuration
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

object BotSecuredAction {
  val TokenHeader = "Token"
}

class BotSecuredAction(configuration: Configuration, cc: ControllerComponents) extends ActionBuilder[Request, AnyContent] {
  private val botKey = configuration.get[String]("auth.bot.key")

  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    request.headers.get(BotSecuredAction.TokenHeader) match {
      case Some(`botKey`) =>
        block(request)
      case _ =>
        Future.successful(Forbidden)
    }
  }

  override protected def executionContext: ExecutionContext = cc.executionContext

  override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser
}
