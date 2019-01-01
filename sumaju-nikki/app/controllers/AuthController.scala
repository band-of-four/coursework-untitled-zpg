package controllers

import javax.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api._
import play.api.mvc._
import play.api.libs.json._
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import utils.auth.CookieAuthEnv
import _root_.services.UserService
import _root_.services.UserService.EmailAlreadyTakenException
import models.User

import scala.concurrent.{ExecutionContext, Future}

object AuthController {
  val SUCCESS = "success"
  val INVALID_CREDS = "invalid_creds"
  val EMAIL_TAKEN = "email_taken"
}

@Singleton
class AuthController @Inject()(cc: ControllerComponents,
                               silhouette: Silhouette[CookieAuthEnv],
                               credentialsProvider: CredentialsProvider,
                               userService: UserService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {
  case class AuthRequest(email: String, password: String)
  implicit val authReads = Json.reads[AuthRequest]

  def signIn() = silhouette.UnsecuredAction(parse.json) async { implicit request =>
    request.body.validate[AuthRequest].fold(
      err => Future.successful(BadRequest(JsError.toJson(err))),
      auth => (for {
        loginInfo <- credentialsProvider.authenticate(Credentials(auth.email, auth.password))
        authenticator <- silhouette.env.authenticatorService.create(loginInfo)
        serializedAuth <- silhouette.env.authenticatorService.init(authenticator)
        user <- userService.retrieve(loginInfo).map(_.get)
        response <- silhouette.env.authenticatorService.embed(serializedAuth, Ok(AuthController.SUCCESS))
      } yield {
        silhouette.env.eventBus.publish(LoginEvent(user, request))
        response
      }).recover {
        case _: ProviderException => UnprocessableEntity(AuthController.INVALID_CREDS)
      }
    )
  }

  def signUp() = silhouette.UnsecuredAction(parse.json) async { implicit request =>
    request.body.validate[AuthRequest].fold(
      err => Future.successful(BadRequest(JsError.toJson(err))),
      auth => userService
        .saveWithPassword(User(auth.email), auth.password)
        .map(_ => Ok(AuthController.SUCCESS))
        .recover {
          case _: EmailAlreadyTakenException => UnprocessableEntity(AuthController.EMAIL_TAKEN)
        }
    )
  }
}
