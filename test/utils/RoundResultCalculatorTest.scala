package utils

import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Ranks, SeriesPlayer}
import models.{ResultLine, RobinGroup, RoundResult, SiteRobinRound}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec

@RunWith(classOf[JUnitRunner])
class RoundResultCalculatorTest extends PlaySpec{


  "RoundResultCalculator" should {


    "Fill the tie scores match makes difference" in {
      val KOEN = SeriesPlayer("1", "1", Player("1","Koen","Van Loock", Ranks.D0), PlayerScores(4,1, 8,4,136, 128))
      val HANS = SeriesPlayer("2", "1",Player("2","Hans","Van Bael", Ranks.E0), PlayerScores(4,1, 8,4,136, 128))
      val ARAM = SeriesPlayer("3", "1",Player("3","Aram","Pauwels", Ranks.B2), PlayerScores(4,1, 8,4,136, 128))

      val KOEN_HANS = PingpongMatch("1",Some(KOEN.player), Some(HANS.player), "1",4, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,17,2),PingpongGame(0,0,2)))
      val KOEN_ARAM = PingpongMatch("2",Some(KOEN.player), Some(ARAM.player), "1",7, false, 21, 2, 2,0, List(PingpongGame(21,19,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))
      val ARAM_HANS = PingpongMatch("3",Some(ARAM.player), Some(HANS.player), "1",7, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))


      RoundResultCalculator.breakTiesWithPlayers(List(KOEN, HANS, ARAM), List(KOEN_HANS, KOEN_ARAM, ARAM_HANS)) mustBe List(
        ResultLine(KOEN, Some(PlayerScores(2,0,4,0,84,69,0))),
        ResultLine(ARAM, Some(PlayerScores(1,1,2,2,79,75,0))),
        ResultLine(HANS, Some(PlayerScores(0,2,0,4,65,84,0))))
    }

    "Fill the tie scores games makes difference" in {
      val KOEN = SeriesPlayer("1", "1", Player("1","Koen","Van Loock", Ranks.D0), PlayerScores(4,1, 8,4,136, 128))
      val HANS = SeriesPlayer("2", "1",Player("2","Hans","Van Bael", Ranks.E0), PlayerScores(4,1, 8,4,136, 128))
      val ARAM = SeriesPlayer("3", "1",Player("3","Aram","Pauwels", Ranks.B2), PlayerScores(4,1, 8,4,136, 128))

      val KOEN_HANS = PingpongMatch("1",Some(KOEN.player), Some(HANS.player), "1",4, true, 21, 2, 2,0, List(PingpongGame(15,21,1),PingpongGame(21,17,2),PingpongGame(18,21,2)))
      val KOEN_ARAM = PingpongMatch("2",Some(KOEN.player), Some(ARAM.player), "1",7, false, 21, 2, 2,0, List(PingpongGame(21,19,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))
      val ARAM_HANS = PingpongMatch("3",Some(ARAM.player), Some(HANS.player), "1",7, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))


      RoundResultCalculator.breakTiesWithPlayers(List(KOEN, HANS, ARAM), List(KOEN_HANS, KOEN_ARAM, ARAM_HANS)) mustBe List(
        ResultLine(KOEN, Some(PlayerScores(1,1,3,2,(42 + 15 + 21 + 18),(42 + 17 + 19 + 18),0))),
        ResultLine(ARAM, Some(PlayerScores(1,1,2,2,(42 + 18 + 19),75,0))),
        ResultLine(HANS, Some(PlayerScores(1,1,2,3, 59 + 33, 15 + 21 + 18 + 42,0))))
    }

    "calculate the roundResult of a bracket" in {}

    "calculate the roundResult of a robinround" in {
      val KOEN = Player("1","Koen","Van Loock", Ranks.D0)
      val HANS = Player("2","Hans","Van Bael", Ranks.E0)
      val ARAM = Player("3","Aram","Pauwels", Ranks.B2)

      val KOEN_HANS = PingpongMatch("1",Some(KOEN), Some(HANS), "1",4, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))
      val KOEN_ARAM = PingpongMatch("2",Some(KOEN), Some(ARAM), "1",7, false, 21, 2, 2,0, List(PingpongGame(21,19,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))
      val ARAM_HANS = PingpongMatch("3",Some(ARAM), Some(HANS), "1",7, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))

      val LUK = Player("4","Luk", "Geraets", Ranks.D6)
      val NICKY = Player("5","Nicky", "Hoste", Ranks.D6)
      val LUDWIG = Player("6","Ludwig", "De Bleser", Ranks.F)

      val LUK_NICKY = PingpongMatch("4",Some(LUK), Some(NICKY), "1",0, true, 21, 2, 2,0, List(PingpongGame(8,21,1),PingpongGame(6,21,2),PingpongGame(0,0,2)))
      val LUK_LUDWIG = PingpongMatch("5",Some(LUK), Some(LUDWIG), "1",5, true, 21, 2, 2,0, List(PingpongGame(21,19,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))
      val NICKY_LUDWIG = PingpongMatch("6",Some(NICKY), Some(LUDWIG), "1",5, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))

      val robinList = List(
        RobinGroup("1", List(SeriesPlayer("1", "1",KOEN, PlayerScores()), SeriesPlayer("2", "1",HANS, PlayerScores()), SeriesPlayer("3", "1",ARAM, PlayerScores())), List(KOEN_HANS, KOEN_ARAM, ARAM_HANS)),
        RobinGroup("1", List(SeriesPlayer("4", "1",LUK, PlayerScores()), SeriesPlayer("5", "1",NICKY, PlayerScores()), SeriesPlayer("6", "1",LUDWIG, PlayerScores())), List(LUK_NICKY, LUK_LUDWIG, NICKY_LUDWIG))
      )
      val robinRound = SiteRobinRound("1",2,"12",1, robinList)

      val results = List(
        ResultLine(SeriesPlayer("1", "1",KOEN, PlayerScores(2,0,4,0,84,70,0)), None),
        ResultLine(SeriesPlayer("4", "1",LUK, PlayerScores(2,0,4,0,56,79,0)),None),
        ResultLine(SeriesPlayer("5", "1",NICKY, PlayerScores(1,1,2,2,84,47,0)), None),
        ResultLine(SeriesPlayer("3", "1",ARAM, PlayerScores(1,1,2,2,79,75,0)),None),
        ResultLine(SeriesPlayer("6", "1",LUDWIG, PlayerScores(0,2,0,4,70,84,0)), None),
        ResultLine(SeriesPlayer("2", "1",HANS, PlayerScores(0,2,0,4,66,84,0)), None)
      )

      RoundResultCalculator.calculateResult(robinRound, true) must equal(results)
    }

  }
}
