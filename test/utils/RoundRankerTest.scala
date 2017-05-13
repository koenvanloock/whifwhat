package utils

import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Ranks, SeriesPlayer}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec

@RunWith(classOf[JUnitRunner])
class RoundRankerTest extends PlaySpec {

  "ResultUtils" should {
    val koen = SeriesPlayer("1", "1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(3,1,6,2,156,132))
    val hans = SeriesPlayer("2", "1", Player("2", "Hans", "Van Bael", Ranks.E4), PlayerScores(3,1,6,2,156,123))
    val luk = SeriesPlayer("3", "1", Player("3", "Luk", "Geraets", Ranks.D6), PlayerScores(1,3,2,6,110,70))
    val lode = SeriesPlayer("4", "1", Player("4", "Lode", "Van Renterghem", Ranks.E6), PlayerScores(0,4,0,8,104))
    val aram = SeriesPlayer("5", "1", Player("5", "Aram", "Pauwels", Ranks.B4), PlayerScores(4,0,8,0,162,120))
    val timFirquet = SeriesPlayer("6", "1", Player("6", "Tim", "Firquet", Ranks.C2), PlayerScores())

    val players = List(
      koen,
      hans,
      luk,
      lode,
      aram,
      timFirquet,
      SeriesPlayer("7", "1", Player("7", "Tim", "Uitdewilligen", Ranks.E0), PlayerScores()),
      SeriesPlayer("8", "1", Player("8", "Matthias", "Lesuise", Ranks.D6), PlayerScores()),
      SeriesPlayer("9", "1", Player("9", "Gil", "Corrujeira-Figueira", Ranks.D0), PlayerScores()),
      SeriesPlayer("10", "1", Player("10", "Timothy", "Donckers", Ranks.D4), PlayerScores()),
      SeriesPlayer("11", "1", Player("11", "Ben", "Kooyman", Ranks.Ng), PlayerScores()),
      SeriesPlayer("12", "1", Player("12", "Arno", "Sels", Ranks.F), PlayerScores()),
      SeriesPlayer("13", "1", Player("13", "Sette", "Van Hoof", Ranks.Rec), PlayerScores()),
      SeriesPlayer("14", "1", Player("14", "Ben", "Verellen", Ranks.Rec), PlayerScores()),
      SeriesPlayer("15", "1", Player("15", "Noa", "Verellen", Ranks.Rec), PlayerScores()),
      SeriesPlayer("16", "1", Player("16", "Sterre", "Roos", Ranks.E4), PlayerScores()))


    val matchListOfFive = List(
      PingpongMatch("1", Some(koen.player), Some(hans.player), "123", 6, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(21, 15, 1), PingpongGame(21, 16, 2))),
      PingpongMatch("2", Some(koen.player), Some(luk.player), "123", 3, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(21, 15, 1), PingpongGame(21, 16, 2))),
      PingpongMatch("3", Some(koen.player), Some(lode.player), "123", 7, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(21, 15, 1), PingpongGame(21, 16, 2))),
      PingpongMatch("4", Some(koen.player), Some(aram.player), "123", 6, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(15, 21, 1), PingpongGame(15, 21, 2))),
      PingpongMatch("5", Some(hans.player), Some(luk.player), "123", 3, isHandicapForB = false, 21, 2, 2, 0, List(PingpongGame(21, 15, 1), PingpongGame(21, 16, 2))),
      PingpongMatch("6", Some(hans.player), Some(lode.player), "123", 1, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(21, 15, 1), PingpongGame(21, 16, 2))),
      PingpongMatch("7", Some(hans.player), Some(aram.player), "123", 12, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(14, 21, 1), PingpongGame(16, 21, 2))),
      PingpongMatch("8", Some(luk.player), Some(lode.player), "123", 4, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(21, 14, 1), PingpongGame(21, 13, 2))),
      PingpongMatch("9", Some(luk.player), Some(aram.player), "123", 9, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(14, 21, 1), PingpongGame(16, 21, 2))),
      PingpongMatch("10", Some(lode.player), Some(aram.player), "123", 6, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(14, 21, 1), PingpongGame(16, 21, 2)))
    )



    "A match with both players in the list is true" in {
      val siteMatch = PingpongMatch("1", Some(koen.player), Some(hans.player), "123", 6, isHandicapForB = true, 21, 2, 0, 2, List(PingpongGame(16, 21, 1), PingpongGame(16, 21, 2)))
      RoundRanker.bothPlayersInList(siteMatch, players) mustBe true
    }

    "A match with only one player in the filtered list is false" in {
      val siteMatch = PingpongMatch("1", Some(koen.player), Some(Player("1", "Jules", "Martens", Ranks.Ng)), "123", 6, isHandicapForB = true, 21, 2, 0, 2, List(PingpongGame(16, 21, 1), PingpongGame(16, 21, 2)))
      RoundRanker.bothPlayersInList(siteMatch, players) mustBe false
    }

    "break tie between two players by considering the direct matchup" in {

      RoundRanker.breakTiesWithDirectMatchup(isWithHandicap = true, players.take(5), matchListOfFive, 4) mustBe List(aram, koen, hans, luk, lode)

    }

    "break tie break between more than two players by considering al matchups between the players" in {

      val koen = SeriesPlayer("1", "1", Player("1", "Koen", "Van Loock", Ranks.D0), PlayerScores(0,4,0,8,156,132))
      val hans = SeriesPlayer("2", "1", Player("2", "Hans", "Van Bael", Ranks.E4), PlayerScores(2,2,4,4,156,123))
      val luk = SeriesPlayer("3", "1", Player("3", "Luk", "Geraets", Ranks.D6), PlayerScores(2,2,4,4,110,70))
      val lode = SeriesPlayer("4", "1", Player("4", "Lode", "Van Renterghem", Ranks.E6), PlayerScores(2,2,4,4,104))
      val aram = SeriesPlayer("5", "1", Player("5", "Aram", "Pauwels", Ranks.B4), PlayerScores(4,0,8,0,162,120))
      val players = List(aram, lode, luk, hans, koen)

      val matchList = List(
        PingpongMatch("1", Some(koen.player), Some(hans.player), "123", 6, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(15, 21, 1), PingpongGame(16, 21, 2))),
        PingpongMatch("2", Some(koen.player), Some(luk.player), "123", 3, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(12, 21, 1), PingpongGame(9, 21, 2))),
        PingpongMatch("3", Some(koen.player), Some(lode.player), "123", 7, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(17, 21, 1), PingpongGame(16, 21, 2))),
        PingpongMatch("4", Some(koen.player), Some(aram.player), "123", 6, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(15, 21, 1), PingpongGame(15, 21, 2))),
        PingpongMatch("5", Some(hans.player), Some(luk.player), "123", 3, isHandicapForB = false, 21, 2, 2, 0, List(PingpongGame(21, 15, 1), PingpongGame(21, 16, 2))),
        PingpongMatch("6", Some(hans.player), Some(lode.player), "123", 1, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(19, 21, 1), PingpongGame(19, 21, 2))),
        PingpongMatch("7", Some(hans.player), Some(aram.player), "123", 12, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(14, 21, 1), PingpongGame(16, 21, 2))),
        PingpongMatch("8", Some(luk.player), Some(lode.player), "123", 4, isHandicapForB = true, 21, 2, 2, 0, List(PingpongGame(21, 14, 1), PingpongGame(21, 13, 2))),
        PingpongMatch("9", Some(luk.player), Some(aram.player), "123", 9, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(14, 21, 1), PingpongGame(16, 21, 2))),
        PingpongMatch("10", Some(lode.player), Some(aram.player), "123", 6, isHandicapForB = false, 21, 2, 0, 2, List(PingpongGame(14, 21, 1), PingpongGame(16, 21, 2)))
      )

      RoundRanker.breakTiesWithDirectMatchup(isWithHandicap = true, players, matchList, 4) mustBe List(aram, hans, luk, lode, koen)
    }

  }

}
