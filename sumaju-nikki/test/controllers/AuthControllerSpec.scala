package test.controllers

import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import test.{AppFactory, DataSpec}

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
}
