package services

import models._
import models.matches.{SiteGame, SiteMatchWithGames, SiteMatch}
import models.player._
import org.scalatestplus.play.PlaySpec
import helpers.TestHelpers._
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.SeriesRoundRepository

import scala.concurrent.Await

class SeriesServiceTest extends PlaySpec{

  val players=  List(
    SeriesPlayer("1","1","1", "Koen","Van Loock", Ranks.D0, PlayerScores()),
    SeriesPlayer("2","2","1", "Hans","Van Bael", Ranks.E4, PlayerScores() ),
    SeriesPlayer("3","3","1", "Luk","Geraets", Ranks.D6, PlayerScores() ),
    SeriesPlayer("4","4","1", "Lode","Van Renterghem", Ranks.E6, PlayerScores()),
    SeriesPlayer("5","5","1", "Tim","Firquet", Ranks.C2, PlayerScores()),
    SeriesPlayer("6","6","1", "Aram","Pauwels", Ranks.B4, PlayerScores()),
    SeriesPlayer("7","7","1", "Tim","Uitdewilligen", Ranks.E0, PlayerScores()),
    SeriesPlayer("8","8","1", "Matthias","Lesuise", Ranks.D6, PlayerScores()),
    SeriesPlayer("9","9","1", "Gil","Corrujeira-Figueira", Ranks.D0, PlayerScores())
  )

  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
  val seriesService = new SeriesService(new DrawService, new SeriesRoundService(new MatchService, seriesRoundRepository))

  "TournamentSeriesService" should {
    "return a list of series of a tournament" in {

      val series = waitFor(seriesService.getTournamentSeriesOfTournament("1"))
      series must contain(TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1"))
    }

    "draw a series" in {
     val test=  seriesService.drawSeries(SiteRobinRound("1234",2,"1",1), players,2,21).get
      test match {
        case robinRound : RobinRound => robinRound.robinList.head.robinPlayers.map(_.seriesPlayerId) mustBe List("1","3","5","7","9")
        case _ => fail("NO robinround found")
      }
    }

    "draw a bracket" in {
      val test=  seriesService.drawSeries(SiteBracketRound("1",2,"1",2), players,2,21).get
      test match {
        case bracket : Bracket =>
          bracket.bracketRounds.length mustBe 2
          bracket.bracketPlayers.length mustBe 4
          val firstRoundMatches = bracket.bracketRounds.head
          firstRoundMatches.head.playerA mustBe Some("1")
          firstRoundMatches.last.playerB mustBe Some("2")
        case _ => fail("NO bracketRound found")
      }
    }
  }

  "advance to the next round if complete" in {
    val series = TournamentSeries("1","Open met voorgift", "#ffffff",2,21,playingWithHandicaps = true, 0,showReferees = false,1, "1")
    val roundList = List(
      RobinRound(List(RobinGroup("1",List(),List(
        SiteMatchWithGames("1", "1","2","1",2,true,21,2,2,0, List(
          SiteGame("1","1",21,15,1),
          SiteGame("1","1",21,15,2))
        ))))),
      RobinRound(List(RobinGroup("2",List(),List()))),
      Bracket("1", List(),List(List()))
    )
    seriesService.advanceSeries(series, roundList).currentRoundNr mustBe 2
  }

  "don't advance to the next round if it's the final one" in {
    val series = TournamentSeries("1","Open met voorgift", "#ffffff",2,21,playingWithHandicaps = true, 0,showReferees = false,3, "1")
    val roundList = List(
      RobinRound(List(RobinGroup("1",List(),List(
        SiteMatchWithGames("1", "1","2","1",2,true,21,2,2,0, List(
          SiteGame("1","1",21,15,1),
          SiteGame("1","1",21,15,2))
        ))))),
      RobinRound(List(RobinGroup("2",List(),List()))),
      Bracket("1", List(),List(List()))
    )
    seriesService.advanceSeries(series, roundList).currentRoundNr mustBe 3
  }

  "calculate SeriesScores" in {

    seriesService.calculateSeriesScores(List(
      SeriesPlayerWithRoundPlayers(
        SeriesPlayer("1", "1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores()),
        List(
          SeriesRoundPlayer("1","1","1","Koen", "Van Loock", Ranks.D0, PlayerScores(3,0,6,2,84,40,304044)),
          SeriesRoundPlayer("1","1","2","Koen", "Van Loock", Ranks.D0, PlayerScores(2,1,5,2,64,40,103024))
        )),
      SeriesPlayerWithRoundPlayers(
        SeriesPlayer("2", "2","1", "Hans", "Van Bael", Ranks.E4, PlayerScores()),
        List(
          SeriesRoundPlayer("2","2","1", "Hans", "Van Bael", Ranks.E4, PlayerScores(3,0,6,2,84,40,304044)),
          SeriesRoundPlayer("2","2","2", "Hans", "Van Bael", Ranks.E4, PlayerScores(2,1,5,2,64,40,103024))
        )),
      SeriesPlayerWithRoundPlayers(
        SeriesPlayer("3", "3","1", "Nicky", "Hoste", Ranks.E4, PlayerScores()),
        List(
          SeriesRoundPlayer("3","3","1","Nicky", "Hoste", Ranks.E4, PlayerScores(3,0,6,2,84,40,304044)),
          SeriesRoundPlayer("3","3","2","Nicky", "Hoste", Ranks.E4, PlayerScores(2,1,5,2,64,40,103024))
        )),
      SeriesPlayerWithRoundPlayers(
        SeriesPlayer("4", "4","1", "Gil", "Corujeira-Figueira", Ranks.E2, PlayerScores()),
        List(
          SeriesRoundPlayer("4","4","1","Gil", "Corujeira-Figueira", Ranks.E2, PlayerScores(3,0,6,2,84,40,304044)),
          SeriesRoundPlayer("4","4","2","Gil", "Corujeira-Figueira", Ranks.E2, PlayerScores(2,1,5,2,64,40,103024))
        ))

    )) mustBe List(
      SeriesPlayer("1", "1","1", "Koen", "Van Loock", Ranks.D0,PlayerScores(5,1,11,4,148,80,407068)),
      SeriesPlayer("2", "2","1", "Hans", "Van Bael", Ranks.E4,PlayerScores(5,1,11,4,148,80,407068)),
      SeriesPlayer("3", "3","1", "Nicky", "Hoste", Ranks.E4,PlayerScores(5,1,11,4,148,80,407068)),
      SeriesPlayer("4", "4","1", "Gil", "Corujeira-Figueira", Ranks.E2,PlayerScores(5,1,11,4,148,80,407068)))
  }


}
