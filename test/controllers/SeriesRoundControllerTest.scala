package controllers

import models.{TournamentSeries, SiteRobinRound, SiteBracketRound}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import repositories.mongo.{SeriesRepository, SeriesRoundRepository}
import helpers.TestHelpers._

import scala.concurrent.Await

@RunWith(classOf[JUnitRunner])
class SeriesRoundControllerTest extends PlaySpec with OneAppPerTest{
  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
  val seriesRepo = appBuilder.injector.instanceOf[SeriesRepository]

  "SeriesRoundController" should {

    "create a default bracket round" in {
      val json = Json.parse("""{"numberOfRobinGroups": 2,"seriesId": "1", "roundNr": 1, "roundType": "R"}""")

      val series = Await.result(seriesRepo.create(TournamentSeries("1","test","#ffffff",2,21,false,0,false,1,"1")),DEFAULT_DURATION)
      val roundCreate = route(app, FakeRequest(POST,s"/seriesrounds/${series.id}"), json).get
      status(roundCreate) mustBe CREATED
      (contentAsJson(roundCreate) \ "seriesId").as[String] mustBe series.id
      (contentAsJson(roundCreate) \ "id").as[String].length mustBe 24
    }

    "update config values of a robin round" in {
      val robinId = Await.result(seriesRoundRepository.create(SiteRobinRound("1",2,"series1",1,Nil)), DEFAULT_DURATION).id
      val json = Json.parse(s"""{"id": "$robinId", "numberOfRobinGroups": 4,"seriesId": "series1", "roundNr": 2, "roundType": "R"}""")

      val roundUpdate = route(app, FakeRequest(PUT,"/seriesrounds"), json).get
      status(roundUpdate) mustBe OK
      (contentAsJson(roundUpdate) \ "id").as[String] mustBe robinId
      (contentAsJson(roundUpdate) \ "numberOfRobinGroups").as[Int] mustBe 4
    }

    "update config values of a bracket round" in {
      val bracketId = Await.result(seriesRoundRepository.create(SiteBracketRound("2",2,"series1",2,Nil, Nil)), DEFAULT_DURATION).id
      val json = Json.parse(s"""{"id": "$bracketId", "numberOfBracketRounds": 4,"seriesId": "series1", "roundNr": 2, "roundType": "B"}""")

      val roundUpdate = route(app, FakeRequest(PUT,"/seriesrounds"), json).get
      status(roundUpdate) mustBe OK
      (contentAsJson(roundUpdate) \ "id").as[String] mustBe bracketId
      (contentAsJson(roundUpdate) \ "numberOfBracketRounds").as[Int] mustBe 4
    }

    "delete a seriesRound" in {
      val bracketId = Await.result(seriesRoundRepository.create(SiteBracketRound("52",2,"series1",2,Nil, Nil)), DEFAULT_DURATION).id
      val delete = route(app, FakeRequest(DELETE,s"/seriesrounds/$bracketId")).get
      status(delete) mustBe NO_CONTENT
    }

  }
}