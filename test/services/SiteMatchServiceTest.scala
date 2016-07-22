package services

import helpers.TestHelpers._
import models.matches.SiteMatch
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.MatchRepository

import scala.concurrent.Await

class SiteMatchServiceTest extends PlaySpec{
  val appBuilder = new GuiceApplicationBuilder().build()
  val matchRepository = appBuilder.injector.instanceOf[MatchRepository]
  val matchService = new SiteMatchService(matchRepository)

  "MatchService" should{
    "return a match by id" in {

      val siteMatch =  waitFor(matchService.getMatch("1"))
      siteMatch mustBe Some(SiteMatch("1","1","2","1",3,true,21,2,0,0))
    }


    "create a match, it recieves an id" in {

      val siteMatch =  Await.result(matchService.create(SiteMatch("1","1","2","1",3,true,21,2,0,0)),DEFAULT_DURATION)
      siteMatch.get.matchId.length mustBe 1
    }

    "update a match" in {
      val createdMatch = Await.result(matchService.create(SiteMatch("2","3","1","1",3,true,21,2,0,0)), DEFAULT_DURATION).get
      val siteMatch = Await.result(matchService.update(SiteMatch(createdMatch.matchId,"4","1","1",3,true,21,2,0,0)), DEFAULT_DURATION).get
        siteMatch.playerA mustBe "4"
    }

    "delete a match" in {
      waitFor(matchService.delete("2"))
      val siteMatch =  waitFor(matchService.getMatch("2"))
      siteMatch mustBe None
    }
  }
}
