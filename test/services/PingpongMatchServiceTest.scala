package services

import helpers.TestHelpers._
import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, Ranks}
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.numongo.repos.MatchRepository

import scala.concurrent.Await

class PingpongMatchServiceTest extends PlaySpec{
  val appBuilder = new GuiceApplicationBuilder().build()
  val matchRepository = appBuilder.injector.instanceOf[MatchRepository]
  val matchService = new SiteMatchService(matchRepository)

  val koen = Player("1", "Koen", "Van Loock", Ranks.D0)
  val hans = Player("2", "Hans", "Van Bael", Ranks.E4)

  "MatchService" should{
    "return a match by id" in {
      val createdMatch =  Await.result(matchService.create(PingpongMatch("1", Some(koen), Some(hans), "1",3,true,21,2,0,0, List(PingpongGame(0,0,1),PingpongGame(0,0,2),PingpongGame(0,0,3)))),DEFAULT_DURATION)
      val siteMatch =  waitFor(matchService.getMatch(createdMatch.id)).get
      siteMatch mustBe createdMatch
    }


    "create a match, it recieves an id" in {

      val siteMatch =  Await.result(matchService.create(PingpongMatch("10",Some(koen),Some(hans),"1",3,true,21,2,0,0, Nil)),DEFAULT_DURATION)
      siteMatch.id mustBe "10"
    }

    "update a match" in {
      val createdMatch = Await.result(matchService.create(PingpongMatch("2",Some(koen),Some(hans),"1",3,isHandicapForB = true,21,2,0,0, Nil)), DEFAULT_DURATION)
      val siteMatch = Await.result(matchService.update(PingpongMatch(createdMatch.id,Some(koen),Some(hans),"1",3,isHandicapForB = true,21,2,0,0, Nil)), DEFAULT_DURATION)
        siteMatch.playerA mustBe Some(koen)
    }

    "delete a match" in {
      waitFor(matchService.delete("2"))
      val siteMatch =  waitFor(matchService.getMatch("2"))
      siteMatch mustBe None
    }
  }
}
