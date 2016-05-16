package services

import helpers.TestHelpers._
import models._
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await


class SeriesRoundServiceTest extends PlaySpec{

  val seriesRoundService = new SeriesRoundService(new MatchService())

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
      val players = List(
        RobinPlayer("1", "1", "1","Koen", "Van Loock", Ranks.D0,0,0,0,0,0,0,0),
        RobinPlayer("2", "1", "2", "Hans", "Van Bael", Ranks.E4,0,0,0,0,0,0,0),
        RobinPlayer("3", "1", "3", "Luk", "Geraets", Ranks.D6,0,0,0,0,0,0,0),
        RobinPlayer("4", "1", "4","Lode", "Van Renterghem", Ranks.E6,0,0,0,0,0,0,0)
      )

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
    val players = List(
      RobinPlayer("1", "1", "1","Koen", "Van Loock", Ranks.D0,0,0,0,0,0,0,0),
      RobinPlayer("2", "1", "2", "Hans", "Van Bael", Ranks.E4,0,0,0,0,0,0,0),
      RobinPlayer("3", "1", "3", "Luk", "Geraets", Ranks.D6,0,0,0,0,0,0,0),
      RobinPlayer("4", "1", "4","Lode", "Van Renterghem", Ranks.E6,0,0,0,0,0,0,0)
    )

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
      BracketPlayer("1", "1","Koen", "Van Loock", Ranks.D0,0,0,0,0,0,0),
      BracketPlayer("1", "2", "Hans", "Van Bael", Ranks.E4,0,0,0,0,0,0),
      BracketPlayer("1", "3", "Luk", "Geraets", Ranks.D6,0,0,0,0,0,0),
      BracketPlayer("1", "4","Lode", "Van Renterghem", Ranks.E6,0,0,0,0,0,0)
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
      BracketPlayer("1", "1","Koen", "Van Loock", Ranks.D0,0,0,0,0,0,0),
      BracketPlayer("1", "2", "Hans", "Van Bael", Ranks.E4,0,0,0,0,0,0),
      BracketPlayer("1", "3", "Luk", "Geraets", Ranks.D6,0,0,0,0,0,0),
      BracketPlayer("1", "4","Lode", "Van Renterghem", Ranks.E6,0,0,0,0,0,0)
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
}
