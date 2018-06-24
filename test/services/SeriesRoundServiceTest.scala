package services

import models.matches.{PingpongGame, PingpongMatch}
import models.player._
import models.types.{BracketLeaf, BracketNode}
import models.{RobinGroup, SiteBracketRound, SiteRobinRound}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.numongo.repos.{SeriesRepository, SeriesRoundRepository}

@RunWith(classOf[JUnitRunner])
class SeriesRoundServiceTest extends PlaySpec{

  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
  val seriesRepository = appBuilder.injector.instanceOf[SeriesRepository]
  val seriesRoundService = new SeriesRoundService(new MatchService(), seriesRoundRepository, seriesRepository)


  val koen = Player("1", "Koen", "Van Loock", Ranks.D0)

  val players = List(
    SeriesPlayer("1", "1", Player("1","Koen", "Van Loock", Ranks.D0),PlayerScores()),
    SeriesPlayer("2", "1", Player("2","Hans", "Van Bael", Ranks.E4),PlayerScores()),
    SeriesPlayer("3", "1", Player("3","Luk", "Geraets", Ranks.D6),PlayerScores()),
    SeriesPlayer("4", "1", Player("1","Lode", "Van Renterghem", Ranks.E6),PlayerScores())
  )


  "SeriesRoundService" should {

    "isRoundComplete of a complete robinRound returns true" in {
      val matchesWithGames: List[PingpongMatch] = List(
        PingpongMatch("1",None,None,"1",6,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
        PingpongMatch("2",None,None,"1",3,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
        PingpongMatch("3",None,None,"1",7,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
        PingpongMatch("4",None,None,"1",3,false,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
        PingpongMatch("5",None,None,"1",1,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
        PingpongMatch("6",None,None,"1",4,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2)))
      )
      seriesRoundService.isRoundComplete(SiteRobinRound("abc123",1,"123",1,List(RobinGroup("1",players,matchesWithGames)))) mustBe true
    }
  }

  "isRoundComplete of an incomplete robinRound returns false" in {
    val matchesWithGames: List[PingpongMatch] = List(
      PingpongMatch("1",None,None,"1",6,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(0,15,2))),
      PingpongMatch("2",None,None,"1",3,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
      PingpongMatch("3",None,None,"1",7,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
      PingpongMatch("4",None,None,"1",3,false,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
      PingpongMatch("5",None,None,"1",1,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2))),
      PingpongMatch("6",None,None,"1",4,true,21,2,2,0,List(PingpongGame(21,16,1),PingpongGame(21,15,2)))
    )

    seriesRoundService.isRoundComplete(SiteRobinRound("abc123",1,"123",1,List(RobinGroup("1",players,matchesWithGames)))) mustBe false
  }

  "isRoundComplete of a complete bracket returns true" in {
    val players = List(
      SeriesPlayer("1", "1", Player("1","Koen", "Van Loock", Ranks.D0),PlayerScores()),
      SeriesPlayer("2", "2", Player("2", "Hans", "Van Bael", Ranks.E4),PlayerScores()),
      SeriesPlayer("3", "3", Player("3", "Luk", "Geraets", Ranks.D6),PlayerScores()),
      SeriesPlayer("4", "4", Player("4","Lode", "Van Renterghem", Ranks.E6),PlayerScores())
    )

    val matches =
      BracketNode[PingpongMatch](PingpongMatch("1", None, None,"3", 6,true,21,2, 2, 0,List(PingpongGame(21,16,1),PingpongGame(21,14,2))),
        BracketLeaf[PingpongMatch](PingpongMatch("1", None, None, "1",7,true,21,2, 2, 0, List(PingpongGame(21,16,1),PingpongGame(22,20,2)))),
        BracketLeaf(PingpongMatch("1", None, None, "1",3,false,21,2, 2, 0, List(PingpongGame(21,16,1),PingpongGame(21,10,2))))
      )
    seriesRoundService.isRoundComplete(SiteBracketRound("1",2,"123",1, players, matches)) mustBe true
  }

  "isRoundComplete of an incomplete bracket returns false" in {
    val players = List(
      SeriesPlayer("1","1", Player("1","Koen", "Van Loock", Ranks.D0),PlayerScores()),
      SeriesPlayer("2","1", Player("2", "Hans", "Van Bael", Ranks.E4),PlayerScores()),
      SeriesPlayer("3","1", Player("3", "Luk", "Geraets", Ranks.D6),PlayerScores()),
      SeriesPlayer("4","1", Player("4","Lode", "Van Renterghem", Ranks.E6),PlayerScores())
    )

    val matches =
      BracketNode[PingpongMatch](PingpongMatch("1", None, None,"3", 6,true,21,2, 1, 0,List(PingpongGame(21,16,1),PingpongGame(0,0,2))),
        BracketLeaf[PingpongMatch](PingpongMatch("1", None, None, "1",7,true,21,2, 2, 0, List(PingpongGame(21,16,1),PingpongGame(22,20,2)))),
        BracketLeaf(PingpongMatch("1", None, None, "1",3,false,21,2, 2, 0, List(PingpongGame(21,16,1),PingpongGame(21,10,2))))
      )

    seriesRoundService.isRoundComplete(SiteBracketRound("1",2,"123",1, players, matches)) mustBe false
  }

  "calculate the correct score of a player in a series" in {
    val playerWithRoundPlayers = SeriesPlayerWithRoundPlayers(SeriesPlayer("1","1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores()),
      List(
        SeriesPlayer("1", "4", Player("1","Koen", "Van Loock", Ranks.D0), PlayerScores(3,0,6,2,84,40,304044)),
        SeriesPlayer("1", "7", Player("2","Koen", "Van Loock", Ranks.D0), PlayerScores(2,1,5,2,64,40,103024))
      ))

    seriesRoundService.calculatePlayerScore(playerWithRoundPlayers) mustBe SeriesPlayer("1","1", Player("1", "Koen", "Van Loock", Ranks.D0),PlayerScores(5,1,11,4,148,80,407068))
  }

  "calculate the roundScore of a winning player with a matchUpdate" in {
    seriesRoundService.updateSeriesPlayerAfterMatch(SeriesPlayer("1","3", koen, PlayerScores()), List(
      PingpongMatch("1", Some(koen), None, "1", 2, true, 21, 2, 2,1, List(PingpongGame(21,0, 1), PingpongGame(15,21, 2),PingpongGame(21,19, 3))),
      PingpongMatch("2", Some(koen), None, "1", 5, true, 21, 2, 2,0, List(PingpongGame(21,15, 1), PingpongGame(23,21, 2)))
    )) mustBe SeriesPlayer("1","3",Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(2,0,4,1,101,76,20402))
  }

  "calculate the roundScore of a losing player with a matchUpdate" in {
    seriesRoundService.updateSeriesPlayerAfterMatch(SeriesPlayer("1","1", Player("1","Koen", "Van Loock", Ranks.D0), PlayerScores()), List(
      PingpongMatch("1", Some(koen), None, "1", 2, true, 21, 2, 0,2, List(PingpongGame(0,21, 1), PingpongGame(9,21, 2))),
      PingpongMatch("2", Some(koen), None, "1", 5, true, 21, 2, 1,2, List(PingpongGame(21,23, 1), PingpongGame(21,16, 2), PingpongGame(19,21, 2)))
    )) mustBe SeriesPlayer("1","1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(0,2,1,4,70,102,51))
  }

  "calculate wins for playerB with a matchUpdate" in {
    seriesRoundService.updateSeriesPlayerAfterMatch(SeriesPlayer("1","1", Player("1","Koen", "Van Loock", Ranks.D0), PlayerScores()), List(
    PingpongMatch("1", Some(koen), None, "1", 2, true, 21, 2, 1,2, List(PingpongGame(12,21, 1),PingpongGame(21,0, 2), PingpongGame(9,21, 3))),
    PingpongMatch("2", None, Some(koen), "1", 5, true, 21, 2, 1,2, List(PingpongGame(21,23, 1), PingpongGame(21,16, 2), PingpongGame(19,21, 2)))
  )) mustBe SeriesPlayer("1","1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(1,1,3,3,102,103,10251))
  }
}
