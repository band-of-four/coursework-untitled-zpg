package test.controllers

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Settings
import com.mohiva.play.silhouette.impl.providers.oauth2.VKProvider
import play.api.libs.json.Json
import play.core.server.Server
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.readers.ArbitraryTypeReader._
import play.api.libs.ws.WSClient
import io.lemonlabs.uri.Url
import io.lemonlabs.uri.dsl._
import controllers.AuthController
import utils.auth.SilhouetteLoader
import test.{AppFactory, DataSpec}

import scala.collection.mutable.ArrayBuffer

class AuthControllerSpec extends DataSpec with AppFactory {
  "password-based auth" should {
    "create and authenticate a user" in {
      val authData = Json.toJson(Map("email" -> "eva00@nerv", "password" -> "..."))

      val signup = route(app, FakeRequest(POST, "/auth/signup").withJsonBody(authData)).get
      contentAsString(signup) mustBe "success"
      status(signup) mustBe OK

      val signin = route(app, FakeRequest(POST, "/auth/signin").withJsonBody(authData)).get
      contentAsString(signin) mustBe "success"
      status(signin) mustBe OK
      cookies(signin).map(_.name) must contain("id")
    }

    "reject invalid credentials" in {
      val authData = Json.toJson(Map("email" -> "eva01@nerv", "password" -> "doormat"))

      val signin = route(app, FakeRequest(POST, "/auth/signin").withJsonBody(authData)).get
      contentAsString(signin) mustBe "invalid_creds"
      status(signin) mustBe UNPROCESSABLE_ENTITY
      cookies(signin).map(_.name) mustNot contain("id")
    }

    "ensure email uniqueness" in {
      val authData = Json.toJson(Map("email" -> "eva02@nerv", "password" -> "nutzlos"))

      val signup = route(app, FakeRequest(POST, "/auth/signup").withJsonBody(authData)).get
      contentAsString(signup) mustBe "success"
      status(signup) mustBe OK

      val duplicateSignup = route(app, FakeRequest(POST, "/auth/signup").withJsonBody(authData)).get
      contentAsString(duplicateSignup) mustBe "email_taken"
      status(duplicateSignup) mustBe UNPROCESSABLE_ENTITY
    }
  }

  "social auth" should {
    val vkConfig = components.configuration.underlying.as[OAuth2Settings]("silhouette.vk")
    val vkAuthUrl = vkConfig.authorizationURL.get
    val vkClientId = vkConfig.clientID
    val vkUserId = 4096

    def withVkClient(authCode: String)(block: WSClient => Unit): Unit = {
      Server.withRouterFromComponents() { components =>
        import Results._
        import components.{defaultActionBuilder => Action}
        import play.api.routing.sird._
      {
        case POST(p"/access_token") =>
          Action { req =>
            val params = req.body.asFormUrlEncoded.get
            params.getOrElse("code", "") mustBe ArrayBuffer(authCode)
            Ok(Json.obj("access_token" -> "AUTH_TOKEN", "expires_in" -> 86326, "user_id" -> vkUserId))
          }
        case GET(p"/api/users.get" ? q"access_token=$token") =>
          Action { req =>
            token mustBe "AUTH_TOKEN"
            Ok(Json.obj("response" -> Json.arr(Json.obj(
              "id" -> vkUserId, "first_name" -> "Lain", "last_name" -> "Iwakura", "photo_max_orig" -> "AVATAR_URL"
            ))))
          }
      }
      } { implicit port => WsTestClient.withClient(block) }
    }

    "create a new user with empty email" in {
      val initialReq = route(app, FakeRequest(GET, "/auth/social/vk")).get
      val initialReqCookies = await(initialReq).newCookies
      val redirect = Url.parse(redirectLocation(initialReq).get)

      redirect.removeQueryString.toString mustBe vkAuthUrl
      redirect.query.param("client_id").get mustBe vkClientId
      redirect.query.param("scope").get mustBe "email"
      redirect.query.param("response_type").get mustBe "code"

      val redirectFromVk = ("/auth/social/vk" ? ("code" -> "AUTH_CODE") & ("state" -> redirect.query.param("state"))).toString

      withVkClient("AUTH_CODE") { client =>
        implicit val materializer = components.materializer
        implicit val ec = components.executionContext

        val silhouette = new SilhouetteLoader(components.configuration, components.userService, client)
        val authController = new AuthController(components.controllerComponents,
          silhouette.env, silhouette.credentialsProvider, silhouette.socialProviderRegistry, components.userService)

        val callbackReq = authController.social("vk").apply(
          FakeRequest(GET, redirectFromVk).withCookies(initialReqCookies:_*))

        contentAsString(callbackReq) mustBe "success"
        status(callbackReq) mustBe OK
        cookies(callbackReq).map(_.name) must contain("id")

        await(callbackReq)
      }

      val userId = components.userLoginInfoDao.findUserIdByLoginInfo(LoginInfo(VKProvider.ID, vkUserId.toString)).get
      val user = components.userDao.findById(userId).get
      user.email mustBe None
    }
  }

}
