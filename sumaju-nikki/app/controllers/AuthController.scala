package controllers

import javax.inject.{Inject, Singleton}
import com.mohiva.play.silhouette.api._
import play.api.mvc._
import play.api.libs.json._
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.providers.{CommonSocialProfileBuilder, CredentialsProvider, SocialProvider, SocialProviderRegistry}
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
                               socialProviderRegistry: SocialProviderRegistry,
                               userService: UserService)
                              (implicit ec: ExecutionContext) extends AbstractController(cc) {
  case class AuthRequest(email: String, password: String)
  implicit val authReads = Json.reads[AuthRequest]

  def signIn() = silhouette.UnsecuredAction(parse.json) async { implicit request =>
    request.body.validate[AuthRequest].fold(
      err => Future.successful(BadRequest(JsError.toJson(err))),
      auth => for {
        loginInfo <- credentialsProvider.authenticate(Credentials(auth.email, auth.password))
        user <- userService.retrieve(loginInfo).map(_.get)
        response <- authenticate(loginInfo, LoginEvent(user, request))
      } yield response
    ).recover {
      case _: ProviderException => UnprocessableEntity(AuthController.INVALID_CREDS)
    }
  }

  def signUp() = silhouette.UnsecuredAction(parse.json) async { implicit request =>
    request.body.validate[AuthRequest].fold(
      err => Future.successful(BadRequest(JsError.toJson(err))),
      auth => userService
        .saveEmail(auth.email, auth.password)
        .map(_ => Ok(AuthController.SUCCESS))
        .recover {
          case _: EmailAlreadyTakenException => UnprocessableEntity(AuthController.EMAIL_TAKEN)
        }
    )
  }

  def social(provider: String) = silhouette.UnsecuredAction(parse.json) async { implicit request =>
    (socialProviderRegistry.get[SocialProvider](provider) match {
      case Some(p: SocialProvider with CommonSocialProfileBuilder) =>
        p.authenticate().flatMap {
          case Left(redirect) => Future.successful(redirect)
          case Right(authInfo) => for {
            profile <- p.retrieveProfile(authInfo)
            user <- userService.saveSocial(profile, authInfo)
            response <- authenticate(profile.loginInfo, LoginEvent(user, request))
          } yield response
        }
      case _ => Future.failed(new ProviderException(s"Unexpected provider $provider"))
    }).recover {
      case _: ProviderException => UnprocessableEntity(AuthController.INVALID_CREDS)
    }
  }

  private def authenticate[T](loginInfo: LoginInfo, loginEvent: LoginEvent[User])(implicit request: Request[T]) = for {
    authenticator <- silhouette.env.authenticatorService.create(loginInfo)
    serializedAuth <- silhouette.env.authenticatorService.init(authenticator)
    response <- silhouette.env.authenticatorService.embed(serializedAuth, Ok(AuthController.SUCCESS))
  } yield {
    silhouette.env.eventBus.publish(loginEvent)
    response
  }
}
