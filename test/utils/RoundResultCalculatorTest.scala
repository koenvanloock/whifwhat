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
        ResultLine(KOEN, None),
        ResultLine(NICKY, None),
        ResultLine(LUK, None),
        ResultLine(ARAM, None),
        ResultLine(HANS, None),
        ResultLine(LUDWIG, None)
      )

      RoundResultCalculator.calculateResult(robinRound) must equal(RoundResult("1","1", results))
    }

  }
}
