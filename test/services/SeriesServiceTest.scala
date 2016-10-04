package services

import models._
import models.matches.{SiteGame, SiteMatch, SiteMatchWithGames}
import models.player._
import org.scalatestplus.play.PlaySpec
import helpers.TestHelpers._
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository, SeriesRoundRepository}

import scala.concurrent.Await

class SeriesServiceTest extends PlaySpec {

  val koen = Player("1", "Koen", "Van Loock", Ranks.D0)
  val hans = Player("2", "Hans", "Van Bael", Ranks.E4)
  val nicky  =Player("12", "Nicky", "Hoste", Ranks.E4)
  val aram = Player("6", "Aram", "Paduwels", Ranks.B4)
  val gil = Player("9", "Gil", "Corujeira-Figueira", Ranks.E2)

  val players = List(
    SeriesPlayer("1","1", koen, PlayerScores()),
    SeriesPlayer("2","1", hans, PlayerScores()),
    SeriesPlayer("3","1", Player("3", "Luk", "Geraets", Ranks.D6), PlayerScores()),
    SeriesPlayer("4","1", Player("4", "Lode", "Van Renterghem", Ranks.E6), PlayerScores()),
    SeriesPlayer("5","1", Player("5", "Tim", "Firquet", Ranks.C2), PlayerScores()),
    SeriesPlayer("6","1", aram, PlayerScores()),
    SeriesPlayer("7","1", Player("7", "Tim", "Uitdewilligen", Ranks.E0), PlayerScores()),
    SeriesPlayer("8","1", Player("8", "Matthias", "Lesuise", Ranks.D6), PlayerScores()),
    SeriesPlayer("9","1", Player("9", "Gil", "Corrujeira-Figueira", Ranks.D0), PlayerScores())
  )

  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
  val seriesPlayerRepository = appBuilder.injector.instanceOf[SeriesPlayerRepository]
  val seriesRepository = appBuilder.injector.instanceOf[SeriesRepository]

  val seriesService = new SeriesService(new SeriesRoundService(new MatchService, seriesRoundRepository), seriesPlayerRepository, seriesRepository, new DrawService)

  "TournamentSeriesService" should {
    "return a list of series of a tournament" in {

      val series = waitFor(seriesService.getTournamentSeriesOfTournament("1"))
      series must contain(TournamentSeries("1", "Open met voorgift", "#ffffff", 2, 21, true, 0, true, 0, "1"))
    }

    //    "draw a series" in {
    //     val test=  seriesService.drawSeries(SiteRobinRound("1234",2,"1",1), players,2,21).get
    //      test match {
    //        case robinRound : RobinRound => robinRound.robinList.head.robinPlayers.map(_.seriesPlayerId) mustBe List("1","3","5","7","9")
    //        case _ => fail("NO robinround found")
    //      }
    //    }

    //    "draw a bracket" in {
    //      val test=  seriesService.drawSeries(SiteBracketRound("1",2,"1",2), players,2,21).get
    //      test match {
    //        case bracket : Bracket =>
    //          bracket.bracketRounds.length mustBe 2
    //          bracket.bracketPlayers.length mustBe 4
    //          val firstRoundMatches = bracket.bracketRounds.head
    //          firstRoundMatches.head.playerA mustBe Some("1")
    //          firstRoundMatches.last.playerB mustBe Some("2")
    //        case _ => fail("NO bracketRound found")
    //      }
    //    }
    //  }

    "advance to the next round if complete" in {
      val series = TournamentSeries("1", "Open met voorgift", "#ffffff", 2, 21, playingWithHandicaps = true, 0, showReferees = false, 1, "1")
      val roundList = List(
        RobinRound(List(RobinGroup("1", List(), List(
          SiteMatch("1", Some(koen), Some(aram),"5", 2, true, 21, 2, 2, 0, List(
            SiteGame(21, 15, 1),
            SiteGame(21, 15, 2))
          ))))),
        RobinRound(List(RobinGroup("2", List(), List()))),
        Bracket("1", List(), List(List()))
      )
      seriesService.advanceSeries(series, roundList).currentRoundNr mustBe 2
    }

    //  "don't advance to the next round if it's the final one" in {
    //    val series = TournamentSeries("1","Open met voorgift", "#ffffff",2,21,playingWithHandicaps = true, 0,showReferees = false,3, "1")
    //    val roundList = List(
    //      RobinRound(List(RobinGroup("1",List(),List(
    //        SiteMatchWithGames("1", "1","2","1",2,true,21,2,2,0, List(
    //          SiteGame("1","1",21,15,1),
    //          SiteGame("1","1",21,15,2))
    //        ))))),
    //      RobinRound(List(RobinGroup("2",List(),List()))),
    //      Bracket("1", List(),List(List()))
    //    )
    //    seriesService.advanceSeries(series, roundList).currentRoundNr mustBe 3
    //  }

    "calculate SeriesScores" in {

      seriesService.calculateSeriesScores(List(
        SeriesPlayerWithRoundPlayers(
          SeriesPlayer("1","1", koen, PlayerScores()),
          List(
            SeriesRoundPlayer("1", "1", "2", koen, PlayerScores(3, 0, 6, 2, 84, 40, 304044)),
            SeriesRoundPlayer("2", "3", "3", koen, PlayerScores(2, 1, 5, 2, 64, 40, 103024))
          )),
        SeriesPlayerWithRoundPlayers(
          SeriesPlayer("2","1",hans, PlayerScores()),
          List(
            SeriesRoundPlayer("2", "2", "1", hans, PlayerScores(3, 0, 6, 2, 84, 40, 304044)),
            SeriesRoundPlayer("2", "2", "2", hans, PlayerScores(2, 1, 5, 2, 64, 40, 103024))
          )),
        SeriesPlayerWithRoundPlayers(
          SeriesPlayer("3","1", nicky, PlayerScores()),
          List(
            SeriesRoundPlayer("3", "3", "1", nicky, PlayerScores(3, 0, 6, 2, 84, 40, 304044)),
            SeriesRoundPlayer("3", "3", "2", nicky, PlayerScores(2, 1, 5, 2, 64, 40, 103024))
          )),
        SeriesPlayerWithRoundPlayers(
          SeriesPlayer("4","1", gil, PlayerScores()),
          List(
            SeriesRoundPlayer("4", "4", "1", gil, PlayerScores(3, 0, 6, 2, 84, 40, 304044)),
            SeriesRoundPlayer("4", "4", "2", gil, PlayerScores(2, 1, 5, 2, 64, 40, 103024))
          ))

      )) mustBe List(
        SeriesPlayer("1","1", koen, PlayerScores(5, 1, 11, 4, 148, 80, 407068)),
        SeriesPlayer("2","1", hans, PlayerScores(5, 1, 11, 4, 148, 80, 407068)),
        SeriesPlayer("3","1", nicky, PlayerScores(5, 1, 11, 4, 148, 80, 407068)),
        SeriesPlayer("4","1", gil, PlayerScores(5, 1, 11, 4, 148, 80, 407068)))
    }


  }
}
