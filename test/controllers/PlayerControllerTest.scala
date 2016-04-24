package controllers

import models.{Player, Ranks}
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

class PlayerControllerTest extends PlaySpec with OneAppPerTest{

  "PlayerController" should {
    "return all players" in {
      val players = route(app, FakeRequest(GET,"/players")).get
      status(players) mustBe OK
    }

  }
}
