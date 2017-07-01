package services

import models.matches.{PingpongGame, PingpongMatch}
import org.scalatestplus.play.PlaySpec

class MatchServiceTest extends PlaySpec{
  "MatchService" should{
    val matchService= new MatchService()
    "count a set for A" in {
      matchService.isForA(21)(PingpongGame(21,18,1)) mustBe true
      matchService.isForA(21)(PingpongGame(22,18,1)) mustBe false
      matchService.isForA(11)(PingpongGame(11,9,1)) mustBe true
      matchService.isForA(11)(PingpongGame(9,11,1)) mustBe false
      matchService.isForA(11)(PingpongGame(15,13,1)) mustBe true
    }

    "count a set for B" in {
      matchService.isForB(11)(PingpongGame(9,11,1)) mustBe true
      matchService.isForB(21)(PingpongGame(29,31,1)) mustBe true
      matchService.isForB(11)(PingpongGame(11,9,1)) mustBe false
      matchService.isForB(11)(PingpongGame(9,11,1)) mustBe true
    }


    "calculateMatchStats calculates the number of sets for A and B" in {
      matchService.calculateMatchStats(PingpongMatch("1",None, None,"1",2,isHandicapForB = true, 21,2, 0,0, List(PingpongGame(21,18,1),PingpongGame(16,21,1),PingpongGame(24,22,1)))) mustBe PingpongMatch("1",None, None,"1",2,isHandicapForB = true, 21,2, 2,1, List(PingpongGame(21,18,1),PingpongGame(16,21,1),PingpongGame(24,22,1)))
    }

    "a complete SiteMatch returns true " in {
      matchService.isMatchComplete(PingpongMatch("1", None, None, "1", 2, isHandicapForB = true, 11, 3, 3,1, List(
        PingpongGame(11, 8, 1),
        PingpongGame(11, 6, 2),
        PingpongGame(3, 11, 3),
        PingpongGame(12, 10, 4)
      ) )) mustBe true
    }

    "an incomplete SiteMatch returns false" in {
      matchService.isMatchComplete(PingpongMatch("1", None, None, "1", 2, isHandicapForB = true, 11, 3, 3,1, List(
        PingpongGame(11, 8, 1),
        PingpongGame(11, 6, 2),
        PingpongGame(3, 11, 3),
        PingpongGame(13, 10, 4)
      ) )) mustBe false
    }
  }

}
