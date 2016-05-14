package services

import models.{Bracket, RobinPlayer, Player, Ranks}
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
      val drawnGroups = drawService.drawSortedRobins(players,1,21,2).get
      drawnGroups.robinList.length mustBe 1
      drawnGroups.robinList.head.robinPlayers.length mustBe 9
      drawnGroups.robinList.head.robinMatches.length mustBe 36
    }

    "draw 2 sorted robins" in {
      val drawnGroups = drawService.drawSortedRobins(players,2,21,2).get
      drawnGroups.robinList.length mustBe 2
      drawnGroups.robinList.head.robinPlayers.length mustBe 5
      drawnGroups.robinList.head.robinMatches.length mustBe 10

      drawnGroups.robinList.last.robinPlayers.length mustBe 4
      drawnGroups.robinList.last.robinMatches.length mustBe 6
    }

    "draw 3 sorted robins" in {
      val drawnGroups = drawService.drawSortedRobins(players,3,21,2).get
      drawnGroups.robinList.length mustBe 3
      drawnGroups.robinList.head.robinPlayers.length mustBe 3
      drawnGroups.robinList.head.robinMatches.length mustBe 3

      drawnGroups.robinList.last.robinPlayers.length mustBe 3
      drawnGroups.robinList.last.robinMatches.length mustBe 3
    }

    "draw 0 sorted robins with 5 as numberOfRobins" in {
       drawService.drawSortedRobins(players,5,21,2) mustBe None
    }

    "draw 0 sorted robins with 0 as numberOfRobins" in {
      drawService.drawSortedRobins(players,0,21,2) mustBe None
    }

    "the ranked random sorts the players by rank and distributes them across the first places in all groups" in{
      val test = drawService.drawRobins(players, 3,21,2, DrawTypes.RankedRandomOrder).get
      println(test.robinList.head.robinPlayers.mkString("\n"))
      val firstPlacesFirstNams = test.robinList.map( robin => robin.robinPlayers.head.firstname)
      firstPlacesFirstNams must contain("Koen")
      firstPlacesFirstNams must contain("Aram")
      firstPlacesFirstNams must contain("Tim")
    }

    "draw a bracket with one round (final)" in {
      val bracket = drawService.drawBracket(players,1,2,21).get

      bracket.bracketPlayers.length mustBe 2
      bracket.bracketRounds.length mustBe 1
      bracket.bracketRounds.head.head.playerA mustBe Some("1")
      bracket.bracketRounds.head.head.playerB mustBe Some("2")
    }

    "draw a bracket with two rounds (semi-finals)" in {
      val bracket = drawService.drawBracket(players,2,2,21).get

      bracket.bracketPlayers.length mustBe 4
      bracket.bracketRounds.length mustBe 2
      bracket.bracketRounds.head.head.playerA mustBe Some("1")
      bracket.bracketRounds.head.head.playerB mustBe Some("4")
      bracket.bracketRounds.head.last.playerA mustBe Some("3")
      bracket.bracketRounds.head.last.playerB mustBe Some("2")

      bracket.bracketRounds.last.head.playerA mustBe None
      bracket.bracketRounds.last.head.playerB mustBe None

    }

    "draw a bracket with two rounds (quarter-finals)" in {
      val bracket = drawService.drawBracket(players,3,2,21).get

      bracket.bracketPlayers.length mustBe 8
      bracket.bracketRounds.length mustBe 3

      bracket.bracketRounds.head.head.playerA mustBe Some("1")
      bracket.bracketRounds.head.head.playerB mustBe Some("8")
      bracket.bracketRounds.head.drop(1).head.playerA mustBe Some("5")
      bracket.bracketRounds.head.drop(1).head.playerB mustBe Some("4")

      bracket.bracketRounds.head.drop(2).head.playerA mustBe Some("3")
      bracket.bracketRounds.head.drop(2).head.playerB mustBe Some("6")
      bracket.bracketRounds.head.last.playerA mustBe Some("7")
      bracket.bracketRounds.head.last.playerB mustBe Some("2")

      bracket.bracketRounds.last.head.playerA mustBe None
      bracket.bracketRounds.last.head.playerB mustBe None

    }
  }
}
