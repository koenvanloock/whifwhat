package services

import models.player.{PlayerScores, SeriesPlayer, Ranks, Player}
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.{SeriesRoundRepository, SeriesRepository, SeriesPlayerRepository}

class DrawServiceTest extends PlaySpec {
  "DrawService" should {
    val appBuilder = new GuiceApplicationBuilder().build()
    val seriesPlayerRepository = appBuilder.injector.instanceOf[SeriesPlayerRepository]
    val seriesRoundRepository = appBuilder.injector.instanceOf[SeriesRoundRepository]
    val seriesRepository = appBuilder.injector.instanceOf[SeriesRepository]
    val matchService = new MatchService
    val drawService = new DrawService
    val players=  List(
      SeriesPlayer("1","1","1", "Koen","Van Loock", Ranks.D0, PlayerScores()),
      SeriesPlayer("2","2","1", "Hans","Van Bael", Ranks.E4, PlayerScores() ),
      SeriesPlayer("3","3","1", "Luk","Geraets", Ranks.D6, PlayerScores() ),
      SeriesPlayer("4","4","1", "Lode","Van Renterghem", Ranks.E6, PlayerScores()),
      SeriesPlayer("5","5","1", "Tim","Firquet", Ranks.C2, PlayerScores()),
      SeriesPlayer("6","6","1", "Aram","Pauwels", Ranks.B4, PlayerScores()),
      SeriesPlayer("7","7","1", "Tim","Uitdewilligen", Ranks.E0, PlayerScores()),
      SeriesPlayer("8","8","1", "Matthias","Lesuise", Ranks.D6, PlayerScores()),
      SeriesPlayer("9","9","1", "Gil","Corrujeira-Figueira", Ranks.D0, PlayerScores())
    )

    val extendedPlayers = players ::: List(
      SeriesPlayer("10","10","1", "Timothy", "Donckers", Ranks.D4, PlayerScores()),
      SeriesPlayer("11","11","1", "Ben", "Kooyman", Ranks.Ng, PlayerScores()),
      SeriesPlayer("12","12","1", "Arno", "Sels", Ranks.F, PlayerScores()),
      SeriesPlayer("13","13","1", "Sette", "Van Hoof", Ranks.Rec, PlayerScores()),
      SeriesPlayer("14","14","1", "Ben", "Verellen", Ranks.Rec, PlayerScores()),
      SeriesPlayer("15","15","1", "Noa", "Verellen", Ranks.Rec, PlayerScores()),
      SeriesPlayer("16","16","1", "Sterre", "Roos", Ranks.E4, PlayerScores()))

    "draw 1 sorted robins" in {
      val drawnGroups = drawService.drawSortedRobins(players, 1, 21, 2).get
      drawnGroups.robinList.length mustBe 1
      drawnGroups.robinList.head.robinPlayers.length mustBe 9
      drawnGroups.robinList.head.robinMatches.length mustBe 36
    }

    "draw 2 sorted robins" in {
      val drawnGroups = drawService.drawSortedRobins(players, 2, 21, 2).get
      drawnGroups.robinList.length mustBe 2
      drawnGroups.robinList.head.robinPlayers.length mustBe 5
      drawnGroups.robinList.head.robinMatches.length mustBe 10

      drawnGroups.robinList.last.robinPlayers.length mustBe 4
      drawnGroups.robinList.last.robinMatches.length mustBe 6
    }

    "draw 3 sorted robins" in {
      val drawnGroups = drawService.drawSortedRobins(players, 3, 21, 2).get
      drawnGroups.robinList.length mustBe 3
      drawnGroups.robinList.head.robinPlayers.length mustBe 3
      drawnGroups.robinList.head.robinMatches.length mustBe 3

      drawnGroups.robinList.last.robinPlayers.length mustBe 3
      drawnGroups.robinList.last.robinMatches.length mustBe 3
    }

    "draw 0 sorted robins with 5 as numberOfRobins" in {
      drawService.drawSortedRobins(players, 5, 21, 2) mustBe None
    }

    "draw 0 sorted robins with 0 as numberOfRobins" in {
      drawService.drawSortedRobins(players, 0, 21, 2) mustBe None
    }

    "the ranked random sorts the players by rank and distributes them across the first places in all groups" in {
      val test = drawService.drawRobins(players, 3, 21, 2, DrawTypes.RankedRandomOrder).get
      val firstPlacesFirstNams = test.robinList.map(robin => robin.robinPlayers.head.firstname)
      firstPlacesFirstNams must contain("Koen")
      firstPlacesFirstNams must contain("Aram")
      firstPlacesFirstNams must contain("Tim")
    }

    "draw random" in {
      val test = drawService.drawRobins(players, 3, 21, 2, DrawTypes.RandomOrder).get
      test.robinList.length mustBe 3
    }

    "a bracket with 0 rounds returns None" in {
      drawService.drawBracket(players, 0,2,21) mustBe None
    }

    "draw a bracket with one round (final)" in {
      val bracket = drawService.drawBracket(players, 1, 2, 21).get

      bracket.bracketPlayers.length mustBe 2
      bracket.bracketRounds.length mustBe 1
      bracket.bracketRounds.head.head.playerA mustBe Some("1")
      bracket.bracketRounds.head.head.playerB mustBe Some("2")
    }

    "draw a bracket with two rounds (semi-finals)" in {
      val bracket = drawService.drawBracket(players, 2, 2, 21).get

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
      val bracket = drawService.drawBracket(players, 3, 2, 21).get

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


    "draw a bracket with 4 rounds (sixteenth-finals)" in {
      val bracket = drawService.drawBracket(extendedPlayers, 4, 2, 21).get

      bracket.bracketPlayers.length mustBe 16
      bracket.bracketRounds.length mustBe 4

      bracket.bracketRounds.head.head.playerA mustBe Some("1")
      bracket.bracketRounds.head.last.playerB mustBe Some("2")

      bracket.bracketRounds.last.head.playerA mustBe None
      bracket.bracketRounds.last.head.playerB mustBe None

    }
  }
}
