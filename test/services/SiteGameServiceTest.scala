package services

import helpers.TestHelpers._
import models.SiteGame
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await

/**
  * @author Koen Van Loock
  * @version 1.0 24/04/2016 0:35
  */
class SiteGameServiceTest extends PlaySpec{
  val siteGameService = new SiteGameService()
  "SiteGameService" should {


    "create a game, it recieves an id" in {
      val siteGame = Await.result(siteGameService.create(SiteGame(None,"1",15,21)), DEFAULT_DURATION)
        siteGame.get.id.get.length mustBe 36
    }

    "update a series" in {
      val createdTournamentSeries = Await.result(siteGameService.create(SiteGame(None,"1",15,21)), DEFAULT_DURATION)
      val series = Await.result(siteGameService.update(SiteGame(None,"1",18,21)), DEFAULT_DURATION).get
      series.pointA mustBe 18
    }
  }

}
