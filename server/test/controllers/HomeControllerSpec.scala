package test.controllers

import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import test.AppFactory

/**
 * For more information, see https://www.playframework.com/documentation/latest/ScalaTestingWithScalaTest
 */
class HomeControllerSpec extends PlaySpec with BaseOneAppPerTest with AppFactory {
  "HomeController GET" should {
    "render the index page" in {
      val request = FakeRequest(GET, "/")
      val home = route(app, request).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("h")
    }
  }
}
