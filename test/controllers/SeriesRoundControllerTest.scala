package controllers

import models._
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play._
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.test._
import play.api.test.Helpers._
import repositories.mongo.{SeriesRepository, SeriesRoundRepository}
import helpers.TestHelpers._
import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Ranks, SeriesPlayer}
import models.types.{BracketLeaf, BracketNode}

import scala.concurrent.Await

@RunWith(classOf[JUnitRunner])
class SeriesRoundControllerTest extends PlaySpec with OneAppPerTest{
  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
  val seriesRepo = appBuilder.injector.instanceOf[SeriesRepository]

  "SeriesRoundController" should {

    "create a default bracket round" in {
      val json = Json.parse("""{"numberOfRobinGroups": 2,"seriesId": "1", "roundNr": 1, "roundType": "R"}""")

      val series = Await.result(seriesRepo.create(TournamentSeries("1","test","#ffffff",2,21,playingWithHandicaps = false,0,showReferees = false,1,"1")),DEFAULT_DURATION)
      val roundCreate = route(app, FakeRequest(POST,s"/seriesrounds/${series.id}"), json).get
      status(roundCreate) mustBe CREATED
      (contentAsJson(roundCreate) \ "seriesId").as[String] mustBe series.id
      (contentAsJson(roundCreate) \ "id").as[String].length mustBe 36
    }

    "update config values of a robin round" in {
      val robinId = Await.result(seriesRoundRepository.create(SiteRobinRound("1",2,"series1",1,Nil)), DEFAULT_DURATION).id
      val json = Json.parse(s"""{"id": "$robinId", "numberOfRobinGroups": 4,"seriesId": "series1", "roundNr": 2, "roundType": "R"}""")

      val roundUpdate = route(app, FakeRequest(PUT,"/seriesroundsconfig"), json).get
      status(roundUpdate) mustBe OK
      (contentAsJson(roundUpdate) \ "id").as[String] mustBe robinId
      (contentAsJson(roundUpdate) \ "numberOfRobinGroups").as[Int] mustBe 4
    }

    "update config values of a bracket round" in {
      val bracketId = Await.result(seriesRoundRepository.create(SiteBracketRound("2",2,"series1",2,Nil, SiteBracket.buildBracket(3,21,2))), DEFAULT_DURATION).id
      val json = Json.parse(s"""{"id": "$bracketId", "numberOfBracketRounds": 4,"seriesId": "series1", "roundNr": 2, "roundType": "B"}""")

      val roundUpdate = route(app, FakeRequest(PUT,"/seriesroundsconfig"), json).get
      status(roundUpdate) mustBe OK
      (contentAsJson(roundUpdate) \ "id").as[String] mustBe bracketId
      (contentAsJson(roundUpdate) \ "numberOfBracketRounds").as[Int] mustBe 4
    }

    "delete a seriesRound" in {
      val bracketId = Await.result(seriesRoundRepository.create(SiteBracketRound("52",2,"series1",2,Nil, SiteBracket.buildBracket(3,21,2))), DEFAULT_DURATION).id
      val delete = route(app, FakeRequest(DELETE,s"/seriesrounds/$bracketId")).get
      status(delete) mustBe NO_CONTENT
    }


    "parse a json bracket with only a leafmatch" in {
      val json = Json.parse("""{"id":"585c679a806f032c0189bd14","numberOfBracketRounds":2,"seriesId":"585c66d0806f03d50089bd00","roundNr":1,"bracketPlayers":[{"id":"8531ad27-7959-48ee-9177-45440ac42edb","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c66f8806f03e90089bd02","firstname":"Koen","lastname":"Van Loock","rank":{"name":"D0","value":10}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}},
                   |{"id":"6a31ddcd-78a4-4114-8e00-266c4b05faa5","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c671f806f03d50089bd04","firstname":"Tim","lastname":"Uitdewilligen","rank":{"name":"E0","value":6}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}},
                   |{"id":"20e564cd-b483-40a9-8710-5052ebd432df","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c6701806f03b60089bd03","firstname":"Hans","lastname":"Van Bael","rank":{"name":"E4","value":4}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}},
                   |{"id":"9c8d41e3-f187-4ae9-94b8-bd140cd36e88","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c672a806f03d50089bd05","firstname":"Matthias","lastname":"Lesuisse","rank":{"name":"D6","value":7}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}}],
                   |"bracketRounds":{"value":{"id":"4117a520-b4d2-476e-85a4-6bd1c35017c7","roundId":"","handicap":0,"isHandicapForB":true,"targetScore":21,"numberOfSetsToWin":2,"wonSetsA":0,"wonSetsB":0,"games":[{"pointA":0,"pointB":0,"gameNr":1},{"pointA":0,"pointB":0,"gameNr":2},{"pointA":0,"pointB":0,"gameNr":3}]}},
                   |"roundType":"B"}
                   |""".stripMargin)

      json.validate[SiteBracketRound](SeriesRoundEvidence.seriesRoundIsModel.siteBracketRoundReads).asOpt mustBe Some(SiteBracketRound("585c679a806f032c0189bd14",2,"585c66d0806f03d50089bd00",1,
        List(SeriesPlayer("8531ad27-7959-48ee-9177-45440ac42edb","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c66f8806f03e90089bd02","Koen", "Van Loock", Ranks.D0),PlayerScores()),
          SeriesPlayer("6a31ddcd-78a4-4114-8e00-266c4b05faa5","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c671f806f03d50089bd04","Tim", "Uitdewilligen", Ranks.E0),PlayerScores()),
          SeriesPlayer("20e564cd-b483-40a9-8710-5052ebd432df","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c6701806f03b60089bd03","Hans", "Van Bael", Ranks.E4),PlayerScores()),
          SeriesPlayer("9c8d41e3-f187-4ae9-94b8-bd140cd36e88","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c672a806f03d50089bd05","Matthias", "Lesuisse", Ranks.D6),PlayerScores())
        ),SiteBracket.createBracket(List(PingpongMatch("4117a520-b4d2-476e-85a4-6bd1c35017c7",None, None, "",0,true, 21, 2, 0,0, List(PingpongGame(0,0,1),PingpongGame(0,0,2),PingpongGame(0,0,3))))), None,"B"))
    }

    "parse a json bracket with a left and right branch" in {
      val json = Json.parse("""{"id":"585c679a806f032c0189bd14","numberOfBracketRounds":2,"seriesId":"585c66d0806f03d50089bd00","roundNr":1,"bracketPlayers":[{"id":"8531ad27-7959-48ee-9177-45440ac42edb","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c66f8806f03e90089bd02","firstname":"Koen","lastname":"Van Loock","rank":{"name":"D0","value":10}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}},
                              |{"id":"6a31ddcd-78a4-4114-8e00-266c4b05faa5","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c671f806f03d50089bd04","firstname":"Tim","lastname":"Uitdewilligen","rank":{"name":"E0","value":6}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}},
                              |{"id":"20e564cd-b483-40a9-8710-5052ebd432df","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c6701806f03b60089bd03","firstname":"Hans","lastname":"Van Bael","rank":{"name":"E4","value":4}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}},
                              |{"id":"9c8d41e3-f187-4ae9-94b8-bd140cd36e88","seriesId":"77eb8f10-474c-46ca-9778-f21e45166f4e","player":{"id":"585c672a806f03d50089bd05","firstname":"Matthias","lastname":"Lesuisse","rank":{"name":"D6","value":7}},"playerScores":{"wonMatches":0,"lostMatches":0,"wonSets":0,"lostSets":0,"wonPoints":0,"lostPoints":0,"totalPoints":0}}],
                              |"bracketRounds":{"value":{"id":"4117a520-b4d2-476e-85a4-6bd1c35017c7","roundId":"","handicap":0,"isHandicapForB":true,"targetScore":21,"numberOfSetsToWin":2,"wonSetsA":0,"wonSetsB":0,"games":[{"pointA":0,"pointB":0,"gameNr":1},{"pointA":0,"pointB":0,"gameNr":2},{"pointA":0,"pointB":0,"gameNr":3}]},
                              |"left":{"value":{"id":"f7b2438d-e799-4666-91ba-f943e1b7429e","roundId":"77eb8f10-474c-46ca-9778-f21e45166f4e","handicap":3,"isHandicapForB":true,"targetScore":21,"numberOfSetsToWin":2,"wonSetsA":0,"wonSetsB":0,"games":[{"pointA":0,"pointB":0,"gameNr":1},{"pointA":0,"pointB":0,"gameNr":2},{"pointA":0,"pointB":0,"gameNr":3}]}},
                              |"right":{"value":{"id":"859a655c-a979-4c03-9b53-5c96de24541b","roundId":"77eb8f10-474c-46ca-9778-f21e45166f4e","handicap":2,"isHandicapForB":true,"targetScore":21,"numberOfSetsToWin":2,"wonSetsA":0,"wonSetsB":0,"games":[{"pointA":0,"pointB":0,"gameNr":1},{"pointA":0,"pointB":0,"gameNr":2},{"pointA":0,"pointB":0,"gameNr":3}]}}},
                              |"roundType":"B"}
                              |""".stripMargin)

      val test = json.validate[SiteBracketRound](SeriesRoundEvidence.seriesRoundIsModel.siteBracketRoundReads).asOpt
      test mustBe Some(SiteBracketRound("585c679a806f032c0189bd14",2,"585c66d0806f03d50089bd00",1,
        List(SeriesPlayer("8531ad27-7959-48ee-9177-45440ac42edb","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c66f8806f03e90089bd02","Koen", "Van Loock", Ranks.D0),PlayerScores()),
          SeriesPlayer("6a31ddcd-78a4-4114-8e00-266c4b05faa5","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c671f806f03d50089bd04","Tim", "Uitdewilligen", Ranks.E0),PlayerScores()),
          SeriesPlayer("20e564cd-b483-40a9-8710-5052ebd432df","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c6701806f03b60089bd03","Hans", "Van Bael", Ranks.E4),PlayerScores()),
          SeriesPlayer("9c8d41e3-f187-4ae9-94b8-bd140cd36e88","77eb8f10-474c-46ca-9778-f21e45166f4e",Player("585c672a806f03d50089bd05","Matthias", "Lesuisse", Ranks.D6),PlayerScores())
        ),BracketNode(PingpongMatch("4117a520-b4d2-476e-85a4-6bd1c35017c7",None, None, "",0,true, 21, 2, 0,0, List(PingpongGame(0,0,1),PingpongGame(0,0,2),PingpongGame(0,0,3))),
          BracketLeaf(PingpongMatch("f7b2438d-e799-4666-91ba-f943e1b7429e",None, None, "77eb8f10-474c-46ca-9778-f21e45166f4e",3,true, 21, 2, 0,0, List(PingpongGame(0,0,1),PingpongGame(0,0,2),PingpongGame(0,0,3)))),
          BracketLeaf(PingpongMatch("859a655c-a979-4c03-9b53-5c96de24541b",None, None, "77eb8f10-474c-46ca-9778-f21e45166f4e",2,true, 21, 2, 0,0, List(PingpongGame(0,0,1),PingpongGame(0,0,2),PingpongGame(0,0,3))))
          ),None, "B"))
    }
  }
}