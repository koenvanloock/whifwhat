package controllers

import models.{Player, Ranks}
import org.scalatestplus.play._
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._

class PlayerControllerTest extends PlaySpec with OneAppPerTest{

  "PlayerController" should {
    "return all players" in {
      val players = route(app, FakeRequest(GET,"/players")).get
      status(players) mustBe OK
    }

    "create a player" in {
      val json = Json.parse("""{"firstname":"Koen","lastname":"Van Loock","rank":{"name":"D0","value":10}}""")

      val playerCreate = route(app, FakeRequest(POST,"/players"), json).get
      status(playerCreate) mustBe CREATED
      (contentAsJson(playerCreate) \ "firstname").as[String] mustBe "Koen"
    }
  }
}
