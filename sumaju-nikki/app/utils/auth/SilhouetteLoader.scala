package utils.auth

import akka.stream.Materializer
import com.mohiva.play.silhouette.api.actions._
import com.mohiva.play.silhouette.api.{Environment, EventBus, Silhouette, SilhouetteProvider}
import com.mohiva.play.silhouette.api.crypto.CrypterAuthenticatorEncoder
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.crypto.{JcaCrypter, JcaCrypterSettings, JcaSigner, JcaSignerSettings}
import com.mohiva.play.silhouette.impl.authenticators.{CookieAuthenticatorService, CookieAuthenticatorSettings}
import com.mohiva.play.silhouette.impl.util.{DefaultFingerprintGenerator, SecureRandomIDGenerator}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import net.ceedubs.ficus.readers.ValueReader
import play.api.{Configuration, mvc}
import play.api.mvc.{Cookie, DefaultCookieHeaderEncoding}

import scala.concurrent.ExecutionContext

object SilhouetteLoader {
  def instantiate(configuration: Configuration,
                  identityService: IdentityService[CookieAuthEnv#I],
                  materializer: Materializer,
                  eventBus: EventBus)
                 (implicit ec: ExecutionContext): Silhouette[CookieAuthEnv] = {
    val crypter = new JcaCrypter(configuration.underlying.as[JcaCrypterSettings]("silhouette.cookieAuthenticator.crypter"))
    val authenticatorEncoder = new CrypterAuthenticatorEncoder(crypter)
    val signer = new JcaSigner(configuration.underlying.as[JcaSignerSettings]("silhouette.cookieAuthenticator.signer"))

    implicit val cookieConfigReader: ValueReader[Option[Cookie.SameSite]] =
      ValueReader.relative(c => Cookie.SameSite.parse(c.as[String]))

    val authenticatorConfig = configuration.underlying.as[CookieAuthenticatorSettings]("silhouette.cookieAuthenticator")
    val authenticatorService = new CookieAuthenticatorService(
      authenticatorConfig,
      repository = None,
      signer,
      cookieHeaderEncoding = new DefaultCookieHeaderEncoding(),
      authenticatorEncoder,
      fingerprintGenerator = new DefaultFingerprintGenerator(),
      idGenerator = new SecureRandomIDGenerator(),
      clock = Clock()
    )

    val env = Environment[CookieAuthEnv](
      identityService,
      authenticatorService,
      Seq(),
      eventBus
    )

    val bodyParser = new mvc.BodyParsers.Default()(materializer)

    val securedReqHandler = new DefaultSecuredRequestHandler(new GlobalSecuredErrorHandler())
    val securedAction = new DefaultSecuredAction(securedReqHandler, bodyParser)
    val unsecuredReqHandler = new DefaultUnsecuredRequestHandler(new GlobalUnsecuredErrorHandler())
    val unsecuredAction = new DefaultUnsecuredAction(unsecuredReqHandler, bodyParser)
    val userAwareReqHandler = new DefaultUserAwareRequestHandler()
    val userAwareAction = new DefaultUserAwareAction(userAwareReqHandler, bodyParser)

    new SilhouetteProvider[CookieAuthEnv](env, securedAction, unsecuredAction, userAwareAction)
  }
}
