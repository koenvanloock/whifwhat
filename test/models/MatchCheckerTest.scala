package models

import java.util.UUID

import models.matches.{MatchChecker, PingpongGame, PingpongMatch}
import models.player.{Player, Ranks}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec

@RunWith(classOf[JUnitRunner])
class MatchCheckerTest extends PlaySpec{


  "MatchChecker" should {

    "check a match with gamescore equal to target score is won and evaluates correctly" in {
      val pingpongMatch = PingpongMatch(UUID.randomUUID().toString, Some(Player("1", "Koen", "Van Loock", Ranks.D0)), Some(Player("1", "Koen", "Van Loock", Ranks.D0)), "1", 2, true,21, 2,0,0,
        List(PingpongGame(21,15,1),PingpongGame(21,23,2), PingpongGame(21,15, 3))
      )
      MatchChecker.calculateSets(pingpongMatch) mustBe pingpongMatch.copy(wonSetsA = 2, wonSetsB = 1)
      MatchChecker.isWon(pingpongMatch) mustBe true
    }

    "check a match without tricky games, strait" in {
      val pingpongMatch = PingpongMatch(UUID.randomUUID().toString, Some(Player("1", "Koen", "Van Loock", Ranks.D0)), Some(Player("1", "Koen", "Van Loock", Ranks.D0)), "1", 2, true,21, 2,0,0,
        List(PingpongGame(21,15,1),PingpongGame(21,18,2), PingpongGame(0,0, 3))
      )
      MatchChecker.calculateSets(pingpongMatch) mustBe pingpongMatch.copy(wonSetsA = 2, wonSetsB = 0)
      MatchChecker.isWon(pingpongMatch) mustBe true
    }

    "check a match with two tricky games: equalling target score, and surpassing it" in {
      val pingpongMatch = PingpongMatch(UUID.randomUUID().toString, Some(Player("1", "Koen", "Van Loock", Ranks.D0)),Some(Player("1", "Koen", "Van Loock", Ranks.D0)), "1", 2, true,21, 2,0,0,
        List(PingpongGame(25,23,1),PingpongGame(21,23,2), PingpongGame(11,21, 3))
      )
      MatchChecker.calculateSets(pingpongMatch) mustBe pingpongMatch.copy(wonSetsA = 1, wonSetsB = 2)
      MatchChecker.isWon(pingpongMatch) mustBe true
    }
  }
}
