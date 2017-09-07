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

    "allowedGamescore returns true on 0-0" in {
      MatchChecker.allowedGameScore(PingpongGame(0, 0,1), 11) mustBe true
      MatchChecker.allowedGameScore(PingpongGame(0, 0,1), 11) mustBe true
    }

    "allowedGamescore returns true on 21-15" in {
      MatchChecker.allowedGameScore(PingpongGame(21, 15,1), 21) mustBe true
      MatchChecker.allowedGameScore(PingpongGame(15, 21,1), 21) mustBe true
    }

    "allowedGamescore returns true on 21-19" in {
      MatchChecker.allowedGameScore(PingpongGame(21, 19,1), 21) mustBe true
      MatchChecker.allowedGameScore(PingpongGame(19, 21,1), 21) mustBe true
    }


    "allowedGamescore returns false on 10-11" in {
      MatchChecker.allowedGameScore(PingpongGame(11, 10,1), 11) mustBe false
      MatchChecker.allowedGameScore(PingpongGame(10, 11,1), 11) mustBe false
    }

    "allowedGamescore returns true on 10-12" in {
      MatchChecker.allowedGameScore(PingpongGame(12, 10,1), 11) mustBe true
      MatchChecker.allowedGameScore(PingpongGame(10, 12,1), 11) mustBe true
    }

    "allowedGamescore returns true on 11-13" in {
      MatchChecker.allowedGameScore(PingpongGame(13, 11,1), 11) mustBe true
      MatchChecker.allowedGameScore(PingpongGame(11, 13,1), 11) mustBe true
    }

    "allowedGamescore returns false on 10-13" in {
      MatchChecker.allowedGameScore(PingpongGame(13, 10,1), 11) mustBe false
      MatchChecker.allowedGameScore(PingpongGame(10, 13,1), 11) mustBe false
    }

    "allowedGamescore returns false on 17-0" in {
      MatchChecker.allowedGameScore(PingpongGame(17, 0,1), 21) mustBe false
      MatchChecker.allowedGameScore(PingpongGame(0, 17,1), 21) mustBe false
    }
  }
}
