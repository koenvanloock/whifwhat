package utils

import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Ranks, SeriesPlayer}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec

@RunWith(classOf[JUnitRunner])
class RoundScorerTest extends PlaySpec{


  "RoundResult" should {

      "calculate a playerScore from a list of matches with subtraction of handicap" in {
        val player = Player("123", "Hans", "Van Bael", Ranks.E4)
        val seriesPlayer = SeriesPlayer("456", "1", player, PlayerScores())

        val matches = List(
          PingpongMatch("1", Some(player), None, "1", 2, isHandicapForB = true, 11, 3, 3, 1, List(
            PingpongGame(11, 8, 1),
            PingpongGame(11, 6, 2),
            PingpongGame(3, 11, 3),
            PingpongGame(12, 10, 4)
          )),
          PingpongMatch("1", None, Some(player), "1", 2, isHandicapForB = true, 11, 3, 3, 1, List(
            PingpongGame(11, 8, 1),
            PingpongGame(11, 6, 2),
            PingpongGame(3, 11, 3),
            PingpongGame(12, 10, 4)
          )
          ))

        RoundScorer.calculatePlayerScore(isWithHandicap = true, matches)(seriesPlayer) mustBe seriesPlayer.copy(playerScores = PlayerScores(1, 1, 4, 4, (11 + 11 + 3 + 12 + 8 + 6 + 11 + 10 - 4 * 2), 64, 0))

      }

    "calculate player scores if the player is b in the match" in {

      val player = Player("123", "Hans", "Van Bael", Ranks.E4)
      val seriesPlayer = SeriesPlayer("456", "1", player, PlayerScores())

      val matches = List(
        PingpongMatch("1", None, Some(player), "1", 2, isHandicapForB = true, 11, 3, 3, 1, List(
          PingpongGame(11, 8, 1),
          PingpongGame(11, 6, 2),
          PingpongGame(3, 11, 3),
          PingpongGame(12, 10, 4)
        )),
        PingpongMatch("1", None, Some(player), "1", 2, isHandicapForB = true, 11, 3, 3, 1, List(
          PingpongGame(11, 8, 1),
          PingpongGame(11, 6, 2),
          PingpongGame(3, 11, 3),
          PingpongGame(12, 10, 4)
        )
        ))

      RoundScorer.calculatePlayerScore(isWithHandicap = false, matches)(seriesPlayer) mustBe seriesPlayer.copy(playerScores = PlayerScores(0, 2, 2, 6, 70, 74, 0))

    }
  }
}
