package services

import models.{Ranks, Player}
import org.scalatestplus.play.PlaySpec

class DrawServiceTest extends PlaySpec{
  "DrawService" should {
    val drawService = new DrawService()
    val players=  List(
      Player("1", "Koen","Van Loock", Ranks.D0 ),
      Player("2", "Hans","Van Bael", Ranks.E4 ),
      Player("3", "Luk","Geraets", Ranks.D6 ),
      Player("4", "Lode","Van Renterghem", Ranks.E6 ),
      Player("5", "Tim","Firquet", Ranks.C2 ),
      Player("6", "Aram","Pauwels", Ranks.B4 ),
      Player("7", "Tim","Uitdewilligen", Ranks.E0 ),
      Player("8", "Matthias","Lesuise", Ranks.D6 ),
      Player("9", "Gil","Corrujeira-Figueira", Ranks.D0 )
    )

    "draw 1 sorted robins" in {
      val drawnGroups = drawService.drawRobins(players,1,21,2)
      println("robinGroup "+ drawnGroups)
      drawnGroups.length mustBe 1
      drawnGroups.head.robinPlayers.length mustBe 9
      drawnGroups.head.robinMatches.length mustBe 36
    }

    "draw 2 sorted robins" in {
      val drawnGroups = drawService.drawRobins(players,2,21,2)
      drawnGroups.length mustBe 2
      drawnGroups.head.robinPlayers.length mustBe 5
      drawnGroups.head.robinMatches.length mustBe 10

      drawnGroups.last.robinPlayers.length mustBe 4
      drawnGroups.last.robinMatches.length mustBe 6
    }

    "draw 3 sorted robins" in {
      val drawnGroups = drawService.drawRobins(players,3,21,2)
      drawnGroups.length mustBe 3
      drawnGroups.head.robinPlayers.length mustBe 3
      drawnGroups.head.robinMatches.length mustBe 3

      drawnGroups.last.robinPlayers.length mustBe 3
      drawnGroups.last.robinMatches.length mustBe 3
    }
  }
}
