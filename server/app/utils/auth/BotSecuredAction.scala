package utils.auth

import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

class BotSecuredAction(cc: ControllerComponents) extends ActionBuilder[Request, AnyContent] {
  override def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]): Future[Result] = {
    request.headers.get("Token") match {
      case Some(token) if token == "KEY-HERE" =>
        block(request)
      case _ =>
        Future.successful(Forbidden)
    }
  }

  override protected def executionContext: ExecutionContext = cc.executionContext

  override def parser: BodyParser[AnyContent] = cc.parsers.defaultBodyParser
}
