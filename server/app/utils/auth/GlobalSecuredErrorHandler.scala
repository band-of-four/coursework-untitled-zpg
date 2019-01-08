package utils.auth

import com.mohiva.play.silhouette.api.actions.SecuredErrorHandler
import play.api.mvc.{RequestHeader, Result}
import play.api.mvc.Results._

import scala.concurrent.Future

class GlobalSecuredErrorHandler extends SecuredErrorHandler {
  override def onNotAuthenticated(implicit request: RequestHeader): Future[Result] =
    Future.successful(Unauthorized)

  override def onNotAuthorized(implicit request: RequestHeader): Future[Result] =
    Future.successful(Forbidden)
}
