package utils

import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Ranks}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec

@RunWith(classOf[JUnitRunner])
class PingpongMatchUtilsTest extends PlaySpec{


  "PingpongMatchUtils" should {

    "return a list of players of a match" in {

      val HANS = Player("2","Hans","Van Bael", Ranks.E0)
      val ARAM = Player("3","Aram","Pauwels", Ranks.B2)

      val ARAM_HANS = PingpongMatch("3",Some(ARAM), Some(HANS), "1",7, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))

      PingpongMatchUtils.getMatchPlayers(ARAM_HANS) must equal(List(ARAM, HANS))
    }

    "return the scores of PlayerA and PlayerB" in {
      val HANS = Player("2","Hans","Van Bael", Ranks.E0)
      val ARAM = Player("3","Aram","Pauwels", Ranks.B2)

      val ARAM_HANS = PingpongMatch("3",Some(ARAM), Some(HANS), "1",7, true, 21, 2, 2,0, List(PingpongGame(21,15,1),PingpongGame(21,18,2),PingpongGame(0,0,2)))

      PingpongMatchUtils.playerAPlayerScores(ARAM_HANS) must equal(PlayerScores(1,0,2,0,42,33, 0))
      PingpongMatchUtils.playerBPlayerScores(ARAM_HANS) must equal(PlayerScores(0,1,0,2,33,42, 0))

    }
  }
}
