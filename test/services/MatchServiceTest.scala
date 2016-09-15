package services

import models.matches.{BracketMatch, SiteGame, SiteMatch, SiteMatchWithGames}
import org.scalatestplus.play.PlaySpec

class MatchServiceTest extends PlaySpec{
  "MatchService" should{
    val matchService= new MatchService()
    "count a set for A" in {
      matchService.isForA(21)(SiteGame(21,18,1)) mustBe true
      matchService.isForA(21)(SiteGame(22,18,1)) mustBe false
      matchService.isForA(11)(SiteGame(11,9,1)) mustBe true
      matchService.isForA(11)(SiteGame(9,11,1)) mustBe false
      matchService.isForA(11)(SiteGame(15,13,1)) mustBe true
    }

    "count a set for B" in {
      matchService.isForB(11)(SiteGame(9,11,1)) mustBe true
      matchService.isForB(21)(SiteGame(29,31,1)) mustBe true
      matchService.isForB(11)(SiteGame(11,9,1)) mustBe false
      matchService.isForB(11)(SiteGame(9,11,1)) mustBe true
    }


    "calculateMatchStats calculates the number of sets for A and B" in {
      matchService.calculateMatchStats(SiteMatch("1",None, None,"1",2,isHandicapForB = true, 21,2, 0,0, List(SiteGame(21,18,1),SiteGame(16,21,1),SiteGame(24,22,1)))) mustBe SiteMatch("1",None, None,"1",2,isHandicapForB = true, 21,2, 2,1, List(SiteGame(21,18,1),SiteGame(16,21,1),SiteGame(24,22,1)))
    }

    "a complete SiteMatch returns true " in {
      matchService.isMatchComplete(SiteMatch("1", None, None, "1", 2, isHandicapForB = true, 11, 3, 3,1, List(
        SiteGame(11, 8, 1),
        SiteGame(11, 6, 2),
        SiteGame(3, 11, 3),
        SiteGame(12, 10, 4)
      ) )) mustBe true
    }

    "an incomplete SiteMatch returns false" in {
      matchService.isMatchComplete(SiteMatch("1", None, None, "1", 2, isHandicapForB = true, 11, 3, 3,1, List(
        SiteGame(11, 8, 1),
        SiteGame(11, 6, 2),
        SiteGame(3, 11, 3),
        SiteGame(13, 10, 4)
      ) )) mustBe false
    }

    "a complete BracketMatch returns true" in {
      matchService.isBracketMatchComplete(BracketMatch("1", "1",  1,2, SiteMatch("1",None, None, "1", 2, isHandicapForB = true, 11, 3, 0, 0, List(
        SiteGame(11, 8, 1),
        SiteGame(11, 6, 2),
        SiteGame(3, 11, 3),
        SiteGame(12, 10, 4)
      ) ))) mustBe true
    }

    "an incomplete BracketMatch returns false" in {
      matchService.isBracketMatchComplete(BracketMatch("1", "1",  1,2, SiteMatch("1",None, None, "1", 2, isHandicapForB = true, 11, 3, 0, 0, List(
        SiteGame(11, 8, 1),
        SiteGame(0, 6, 2),
        SiteGame(3, 11, 3),
        SiteGame(12, 10, 4)
      ) ))) mustBe false
    }
  }

}
