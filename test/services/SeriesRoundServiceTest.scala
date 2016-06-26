package services

import helpers.TestHelpers._
import models.{Bracket, RobinRound, RobinGroup, SiteRobinRound}
import models.matches.{BracketMatchWithGames, SiteGame, SiteMatchWithGames}
import models.player._
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.SeriesRoundRepository

import scala.concurrent.Await


class SeriesRoundServiceTest extends PlaySpec{

  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
  val seriesRoundService = new SeriesRoundService(new MatchService(), seriesRoundRepository)
  val players = List(
    SeriesRoundPlayer("1", "1","1", "Koen", "Van Loock", Ranks.D0,PlayerScores()),
    SeriesRoundPlayer("2", "2","1", "Hans", "Van Bael", Ranks.E4,PlayerScores()),
    SeriesRoundPlayer("3", "3","1", "Luk", "Geraets", Ranks.D6,PlayerScores()),
    SeriesRoundPlayer("4", "4","1", "Lode", "Van Renterghem", Ranks.E6,PlayerScores())
  )


  "SeriesRoundService" should {
    "return a single seriesRound" in {

      val series = waitFor(seriesRoundService.getSeriesRound("1"))
      series must contain(SiteRobinRound("1",2,"1",1))
    }

    /*
    "create a seriesRound, he/she recieves an id" in {
      val series = Await.result(seriesRoundService.create(RobinRound(Some("1"),2,"1",1)), DEFAULT_DURATION)
      series.get.seriesId.get.length mustBe 36
    }

    "update a series" in {
      val createdTournamentSeries = Await.result(seriesService.create(TournamentSeries(None, "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION)
      val series = Await.result(seriesService.update(TournamentSeries(None, "Open zonder voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION).get
      series.seriesName mustBe "Open zonder voorgift"
    }*/

    "delete a seriesRound" in {
      // insert 5 here once integration works
      waitFor(seriesRoundService.delete("5"))
      val series = waitFor(seriesRoundService.getSeriesRound("5"))
      series mustBe None
    }


    "isRoundComplete of a complete robinRound returns true" in {


      val matchesWithGames = List(
        SiteMatchWithGames("1","1","2","1",6,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("2","1","3","1",3,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("3","1","4","1",7,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("4","2","3","1",3,false,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("5","2","4","1",1,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("6","3","4","1",4,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2)))
      )

      seriesRoundService.isRoundComplete(RobinRound(List(RobinGroup("1",players,matchesWithGames)))) mustBe true
    }
  }

  "isRoundComplete of an incomplete robinRound returns false" in {
    val matchesWithGames = List(
      SiteMatchWithGames("1","1","2","1",6,true,21,2,1,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",0,0,2))),
      SiteMatchWithGames("2","1","3","1",3,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("3","1","4","1",7,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("4","2","3","1",3,false,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("5","2","4","1",1,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2))),
      SiteMatchWithGames("6","3","4","1",4,true,21,2,2,0,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,15,2)))
    )

    seriesRoundService.isRoundComplete(RobinRound(List(RobinGroup("1",players,matchesWithGames)))) mustBe false
  }

  "isRoundComplete of a complete bracket returns true" in {
    val players = List(
      BracketPlayer("1", "1","Koen", "Van Loock", Ranks.D0,PlayerScores()),
      BracketPlayer("1", "2", "Hans", "Van Bael", Ranks.E4,PlayerScores()),
      BracketPlayer("1", "3", "Luk", "Geraets", Ranks.D6,PlayerScores()),
      BracketPlayer("1", "4","Lode", "Van Renterghem", Ranks.E6,PlayerScores())
    )

    val matches =
      List(
        List(
          BracketMatchWithGames("1","1",1,1,"1", Some("1"), Some("4"),7,true,21,2,List(SiteGame("1","1",21,16,1),SiteGame("2","1",22,20,2))),
          BracketMatchWithGames("2","1",1,2,"1", Some("2"), Some("3"),3,false,21,2,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,10,2)))
        ),
        List(BracketMatchWithGames("3","1",2,1,"1", Some("1"), Some("2"),6,true,21,2,List(SiteGame("1","1",21,16,1),SiteGame("2","1",21,14,2))))
      )

    seriesRoundService.isRoundComplete(Bracket("1", players, matches)) mustBe true
  }

  "isRoundComplete of an incomplete bracket returns false" in {
    val players = List(
      BracketPlayer("1", "1","Koen", "Van Loock", Ranks.D0,PlayerScores()),
      BracketPlayer("1", "2", "Hans", "Van Bael", Ranks.E4,PlayerScores()),
      BracketPlayer("1", "3", "Luk", "Geraets", Ranks.D6,PlayerScores()),
      BracketPlayer("1", "4","Lode", "Van Renterghem", Ranks.E6,PlayerScores())
    )

    val matches =
      List(
          List(
            BracketMatchWithGames("1","1",1,1,"1", Some("1"), Some("4"),7,true,21,2,List(SiteGame("1","1",21,16,1),SiteGame("2","1",0,0,2))),
            BracketMatchWithGames("2","1",1,2,"1", Some("2"), Some("3"),3,false,21,2,List(SiteGame("1","1",21,16,1),SiteGame("2","1",0,0,2)))
            ),
          List(BracketMatchWithGames("3","1",2,1,"1", Some("1"), Some("2"),6,true,21,2,List(SiteGame("1","1",21,16,1),SiteGame("2","1",0,0,2))))
      )

    seriesRoundService.isRoundComplete(Bracket("1", players, matches)) mustBe false
  }

  "calculate the correct score of a player in a series" in {
    val playerWithRoundPlayers = SeriesPlayerWithRoundPlayers(SeriesPlayer("1", "1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores()),
      List(
        SeriesRoundPlayer("1","1","1","Koen", "Van Loock", Ranks.D0, PlayerScores(3,0,6,2,84,40,304044)),
        SeriesRoundPlayer("1","1","2","Koen", "Van Loock", Ranks.D0, PlayerScores(2,1,5,2,64,40,103024))
      ))

    seriesRoundService.calculatePlayerScore(playerWithRoundPlayers) mustBe SeriesPlayer("1", "1","1", "Koen", "Van Loock", Ranks.D0,PlayerScores(5,1,11,4,148,80,407068))
  }

  "calculate the roundScore of a winning player with a matchUpdate" in {
    seriesRoundService.updateSeriesRoundPlayerAfterMatch(SeriesRoundPlayer("1","1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores()), List(
      SiteMatchWithGames("1", "1", "2", "1", 2, true, 21, 2, 2,1, List(SiteGame("1", "1", 21,0, 1), SiteGame("2", "1", 15,21, 2),SiteGame("3", "1", 21,19, 3))),
      SiteMatchWithGames("2", "1", "3", "1", 5, true, 21, 2, 2,0, List(SiteGame("4", "2", 21,15, 1), SiteGame("5", "2", 23,21, 2)))
    )) mustBe SeriesRoundPlayer("1","1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores(2,0,4,1,101,76,20402))
  }

  "calculate the roundScore of a losing player with a matchUpdate" in {
    seriesRoundService.updateSeriesRoundPlayerAfterMatch(SeriesRoundPlayer("1","1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores()), List(
      SiteMatchWithGames("1", "1", "2", "1", 2, true, 21, 2, 0,2, List(SiteGame("1", "1", 0,21, 1), SiteGame("2", "1", 9,21, 2))),
      SiteMatchWithGames("2", "1", "3", "1", 5, true, 21, 2, 1,2, List(SiteGame("3", "2", 21,23, 1), SiteGame("4", "2", 21,16, 2), SiteGame("5", "2", 19,21, 2)))
    )) mustBe SeriesRoundPlayer("1","1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores(0,2,1,4,70,102,51))
  }

  "calculate wins for playerB with a matchUpdate" in {
    seriesRoundService.updateSeriesRoundPlayerAfterMatch(SeriesRoundPlayer("2","1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores()), List(
    SiteMatchWithGames("1", "1", "2", "1", 2, true, 21, 2, 1,2, List(SiteGame("1", "1", 12,21, 1),SiteGame("2", "1", 21,0, 2), SiteGame("3", "1", 9,21, 3))),
    SiteMatchWithGames("2", "1", "3", "1", 5, true, 21, 2, 1,2, List(SiteGame("4", "2", 21,23, 1), SiteGame("5", "2", 21,16, 2), SiteGame("6", "2", 19,21, 2)))
  )) mustBe SeriesRoundPlayer("2","1","1", "Koen", "Van Loock", Ranks.D0, PlayerScores(1,0,2,1,42,42,10201))
  }
}
