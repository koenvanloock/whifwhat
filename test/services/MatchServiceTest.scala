package services

import models.{SiteMatchWithGames, SiteGame}
import org.scalatestplus.play.PlaySpec

class MatchServiceTest extends PlaySpec{
  "MatchService" should{
    val matchService= new MatchService()
    "count a set for A" in {
      matchService.isForA(21)(SiteGame("1","1",21,18,1)) mustBe true
      matchService.isForA(21)(SiteGame("1","1",22,18,1)) mustBe false
      matchService.isForA(11)(SiteGame("1","1",11,9,1)) mustBe true
      matchService.isForA(11)(SiteGame("1","1",9,11,1)) mustBe false
      matchService.isForA(11)(SiteGame("1","1",15,13,1)) mustBe true
    }

    "count a set for B" in {
      matchService.isForB(11)(SiteGame("1","1",9,11,1)) mustBe true
      matchService.isForB(21)(SiteGame("1","1",29,31,1)) mustBe true
      matchService.isForB(11)(SiteGame("1","1",11,9,1)) mustBe false
      matchService.isForB(11)(SiteGame("1","1",9,11,1)) mustBe true
    }


    "calculateMatchStats calculates the number of sets for A and B" in {
      matchService.calculateMatchStats(SiteMatchWithGames("1","1","2",2,isHandicapForB = true, 21,2, 0,0, List(SiteGame("1","1",21,18,1),SiteGame("1","1",16,21,1),SiteGame("1","1",24,22,1)))) mustBe SiteMatchWithGames("1","1","2",2,isHandicapForB = true, 21,2, 2,1, List(SiteGame("1","1",21,18,1),SiteGame("1","1",16,21,1),SiteGame("1","1",24,22,1)))
    }
  }

}
