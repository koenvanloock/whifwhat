package services

import helpers.TestHelpers._
import models.{Bracket, RobinGroup, RobinRound, SiteRobinRound}
import models.matches.{BracketMatch, SiteGame, SiteMatch, SiteMatchWithGames}
import models.player._
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.mongo.SeriesRoundRepository

import scala.concurrent.Await


class SeriesRoundServiceTest extends PlaySpec{

  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
  val seriesRoundService = new SeriesRoundService(new MatchService(), seriesRoundRepository)

  val koen = Player("1", "Koen", "Van Loock", Ranks.D0)

  val players = List(
    SeriesRoundPlayer("1", "1","1", Player("1","Koen", "Van Loock", Ranks.D0),PlayerScores()),
    SeriesRoundPlayer("2", "2","1", Player("2","Hans", "Van Bael", Ranks.E4),PlayerScores()),
    SeriesRoundPlayer("3", "3","1", Player("3","Luk", "Geraets", Ranks.D6),PlayerScores()),
    SeriesRoundPlayer("4", "4","1", Player("1","Lode", "Van Renterghem", Ranks.E6),PlayerScores())
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


      val matchesWithGames: List[SiteMatch] = List(
        SiteMatch("1",None,None,"1",6,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
        SiteMatch("2",None,None,"1",3,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
        SiteMatch("3",None,None,"1",7,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
        SiteMatch("4",None,None,"1",3,false,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
        SiteMatch("5",None,None,"1",1,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
        SiteMatch("6",None,None,"1",4,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2)))
      )

      seriesRoundService.isRoundComplete(RobinRound(List(RobinGroup("1",players,matchesWithGames)))) mustBe true
    }
  }

  "isRoundComplete of an incomplete robinRound returns false" in {
    val matchesWithGames: List[SiteMatch] = List(
      SiteMatch("1",None,None,"1",6,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(0,15,2))),
      SiteMatch("2",None,None,"1",3,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
      SiteMatch("3",None,None,"1",7,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
      SiteMatch("4",None,None,"1",3,false,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
      SiteMatch("5",None,None,"1",1,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2))),
      SiteMatch("6",None,None,"1",4,true,21,2,2,0,List(SiteGame(21,16,1),SiteGame(21,15,2)))
    )

    seriesRoundService.isRoundComplete(RobinRound(List(RobinGroup("1",players,matchesWithGames)))) mustBe false
  }

  "isRoundComplete of a complete bracket returns true" in {
    val players = List(
      BracketPlayer("1", "1", Player("1","Koen", "Van Loock", Ranks.D0),PlayerScores()),
      BracketPlayer("2", "2", Player("2", "Hans", "Van Bael", Ranks.E4),PlayerScores()),
      BracketPlayer("3", "3", Player("3", "Luk", "Geraets", Ranks.D6),PlayerScores()),
      BracketPlayer("4", "4", Player("4","Lode", "Van Renterghem", Ranks.E6),PlayerScores())
    )

    val matches =
      List(
        List(
          BracketMatch("1","1",1,1, SiteMatch("1", None, None, "1",7,true,21,2, 0, 0, List(SiteGame(21,16,1),SiteGame(22,20,2)))),
          BracketMatch("2","1",1,2, SiteMatch("1", None, None, "1",3,false,21,2, 0, 0, List(SiteGame(21,16,1),SiteGame(21,10,2))))
        ),
        List(BracketMatch("3","1",2,1, SiteMatch("1", None, None,"3", 6,true,21,2, 0, 0,List(SiteGame(21,16,1),SiteGame(21,14,2)))))
      )

    seriesRoundService.isRoundComplete(Bracket("1", players, matches)) mustBe true
  }

  "isRoundComplete of an incomplete bracket returns false" in {
    val players = List(
      BracketPlayer("1","1", Player("1","Koen", "Van Loock", Ranks.D0),PlayerScores()),
      BracketPlayer("2","1", Player("2", "Hans", "Van Bael", Ranks.E4),PlayerScores()),
      BracketPlayer("3","1", Player("3", "Luk", "Geraets", Ranks.D6),PlayerScores()),
      BracketPlayer("4","1", Player("4","Lode", "Van Renterghem", Ranks.E6),PlayerScores())
    )

    val matches =
      List(
          List(
            BracketMatch("1","1",1,1, SiteMatch("1", None, None, "1",7,true,21,2, 0, 0, List(SiteGame(21,16,1),SiteGame(0,0,2)))),
            BracketMatch("2","1",1,2, SiteMatch("1", None, None, "1",3,false,21,2, 0, 0, List(SiteGame(21,16,1),SiteGame(21,10,2))))
          ),
        List(BracketMatch("3","1",2,1, SiteMatch("1", None, None,"3", 6,true,21,2, 0, 0,List(SiteGame(21,16,1),SiteGame(0,0,2)))))
      )

    seriesRoundService.isRoundComplete(Bracket("1", players, matches)) mustBe false
  }

  "calculate the correct score of a player in a series" in {
    val playerWithRoundPlayers = SeriesPlayerWithRoundPlayers(SeriesPlayer("1","1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores()),
      List(
        SeriesRoundPlayer("1","1", "4", Player("1","Koen", "Van Loock", Ranks.D0), PlayerScores(3,0,6,2,84,40,304044)),
        SeriesRoundPlayer("1","1", "7", Player("2","Koen", "Van Loock", Ranks.D0), PlayerScores(2,1,5,2,64,40,103024))
      ))

    seriesRoundService.calculatePlayerScore(playerWithRoundPlayers) mustBe SeriesPlayer("1","1", Player("1", "Koen", "Van Loock", Ranks.D0),PlayerScores(5,1,11,4,148,80,407068))
  }

  "calculate the roundScore of a winning player with a matchUpdate" in {
    seriesRoundService.updateSeriesRoundPlayerAfterMatch(SeriesRoundPlayer("1", "2","3", koen, PlayerScores()), List(
      SiteMatch("1", Some(koen), None, "1", 2, true, 21, 2, 2,1, List(SiteGame(21,0, 1), SiteGame(15,21, 2),SiteGame(21,19, 3))),
      SiteMatch("2", Some(koen), None, "1", 5, true, 21, 2, 2,0, List(SiteGame(21,15, 1), SiteGame(23,21, 2)))
    )) mustBe SeriesRoundPlayer("1", "2","3",Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(2,0,4,1,101,76,20402))
  }

  "calculate the roundScore of a losing player with a matchUpdate" in {
    seriesRoundService.updateSeriesRoundPlayerAfterMatch(SeriesRoundPlayer("1","1","1", Player("1","Koen", "Van Loock", Ranks.D0), PlayerScores()), List(
      SiteMatch("1", Some(koen), None, "1", 2, true, 21, 2, 0,2, List(SiteGame(0,21, 1), SiteGame(9,21, 2))),
      SiteMatch("2", Some(koen), None, "1", 5, true, 21, 2, 1,2, List(SiteGame(21,23, 1), SiteGame(21,16, 2), SiteGame(19,21, 2)))
    )) mustBe SeriesRoundPlayer("1","1","1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(0,2,1,4,70,102,51))
  }

  "calculate wins for playerB with a matchUpdate" in {
    seriesRoundService.updateSeriesRoundPlayerAfterMatch(SeriesRoundPlayer("1","1","1", Player("1","Koen", "Van Loock", Ranks.D0), PlayerScores()), List(
    SiteMatch("1", Some(koen), None, "1", 2, true, 21, 2, 1,2, List(SiteGame(12,21, 1),SiteGame(21,0, 2), SiteGame(9,21, 3))),
    SiteMatch("2", None, Some(koen), "1", 5, true, 21, 2, 1,2, List(SiteGame(21,23, 1), SiteGame(21,16, 2), SiteGame(19,21, 2)))
  )) mustBe SeriesRoundPlayer("1","1","1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(1,1,3,3,102,103,10251))
  }
}
