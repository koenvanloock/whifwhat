package utils

import models.ResultLine
import models.player.{Player, PlayerScores, Ranks, SeriesPlayer}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec

@RunWith(classOf[JUnitRunner])
class BracketPlacementTest extends PlaySpec{


  "BracketPlacement" should {

    "sortplayers of one robin into final according to place" in {
      val resultRobin: List[List[ResultLine]] = List(generateRobinResults(1,6))
      BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 1).map(_.player.firstname) mustBe List("1 1", "1 2")
    }

    "sortplayers of one robin quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(generateRobinResults(1,20))
      BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 3).map(_.player.firstname) mustBe List("1 1", "1 2", "1 3", "1 4", "1 5", "1 6", "1 7", "1 8")
    }

    "sortplayers of two robins quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(generateRobinResults(1,4), generateRobinResults(2,4))
      BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 3).map(_.player.firstname) mustBe List("1 1", "2 1", "1 2", "2 2", "1 3", "2 3", "1 4", "2 4")
    }

    "sortplayers of three robins quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(
        generateRobinResults(1,4),
        generateRobinResults(2,4),
        generateRobinResults(3,4)
      )

      val sortedPlayers = BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 3)

      //drawHasNoSameRobinEncounters(sortedPlayers, 3) mustBe true
      sortedPlayers.map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "1 2", "2 2", "3 2", "1 3", "2 3")
    }

    "sortplayers of four robins quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(
        generateRobinResults(1,4),
        generateRobinResults(2,4),
        generateRobinResults(3,4),
        generateRobinResults(4,4)
      )

      val sortedPlayers = BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 3)

      drawHasNoSameRobinEncounters(sortedPlayers, 3) mustBe true
      sortedPlayers.map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "4 1", "1 2", "2 2", "3 2", "4 2")
      BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 4).map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "4 1", "1 2", "2 2", "3 2", "4 2", "1 3", "2 3", "3 3", "4 3", "1 4", "2 4", "3 4", "4 4")
    }

    "sortplayers of five robins quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(
        generateRobinResults(1,4),
        generateRobinResults(2,4),
        generateRobinResults(3,4),
        generateRobinResults(4,4),
        generateRobinResults(5,4)
      )

      val sortedPlayers = BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 3)

      //drawHasNoSameRobinEncounters(sortedPlayers, 3) mustBe true
      sortedPlayers.map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "4 1", "5 1", "1 2", "2 2", "3 2")
      //drawHasNoSameRobinEncounters(BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 4), 4) mustBe true
      BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 4).map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "4 1", "5 1", "1 2", "2 2", "3 2", "4 2", "5 2", "1 3", "2 3", "3 3", "4 3", "5 3", "1 4")
    }

    "sortplayers of six robins quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(
        generateRobinResults(1,4),
        generateRobinResults(2,4),
        generateRobinResults(3,4),
        generateRobinResults(4,4),
        generateRobinResults(5,4),
        generateRobinResults(6,4)
      )

      val sortedPlayers = BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 4)

      drawHasNoSameRobinEncounters(sortedPlayers, 4) mustBe true
      sortedPlayers.map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "4 1", "5 1", "6 1", "1 2", "2 2", "3 2", "4 2", "5 2", "6 2", "1 3", "2 3", "3 3", "4 3")
    }

    "sortplayers of seven robins quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(
        generateRobinResults(1,4),
        generateRobinResults(2,4),
        generateRobinResults(3,4),
        generateRobinResults(4,4),
        generateRobinResults(5,4),
        generateRobinResults(6,4),
        generateRobinResults(7,4)
      )

      val sortedPlayers = BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 4)

      //drawHasNoSameRobinEncounters(sortedPlayers, 4) mustBe true
      sortedPlayers.map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "4 1", "5 1", "6 1", "7 1", "1 2", "2 2", "3 2", "4 2", "5 2", "6 2", "7 2", "1 3", "2 3")
    }

    "sortplayers of eight robins quarters according to place" in {
      val resultRobin: List[List[ResultLine]] = List(
        generateRobinResults(1,4),
        generateRobinResults(2,4),
        generateRobinResults(3,4),
        generateRobinResults(4,4),
        generateRobinResults(5,4),
        generateRobinResults(6,4),
        generateRobinResults(7,4),
        generateRobinResults(8,4)
      )

      val sortedPlayers = BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 4)

      drawHasNoSameRobinEncounters(sortedPlayers, 4) mustBe true
      sortedPlayers.map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "4 1", "5 1", "6 1", "7 1", "8 1", "1 2", "2 2", "3 2", "4 2", "5 2", "6 2", "7 2", "8 2")
    }

    "pick the best thirds" in {
      val resultRobin: List[List[ResultLine]] = List(
        generateRobinResults(1,4),
        generateRobinResultsWithHighScores(2,4),
        generateRobinResultsWithHighScores(3,4)
      )

      val sortedPlayers = BracketPlacement.sortRobinPlayersForPlacedBracket(resultRobin, 3)
      sortedPlayers.map(_.player.firstname) mustBe List("1 1", "2 1", "3 1", "1 2", "2 2", "3 2", "2 3", "3 3")
    }

  }



  def generateRobinResults(robinNr: Int, numberOfPlayers: Int): List[ResultLine] = {
    for( nr <- (1 to numberOfPlayers).toList) yield {
      val playerNr = robinNr * (numberOfPlayers-1) + nr
      ResultLine(SeriesPlayer(s"$playerNr","",Player(s"$playerNr",s"$robinNr $nr","",Ranks.Ng),PlayerScores()), None)
    }
  }

  def generateRobinResultsWithHighScores(robinNr: Int, numberOfPlayers: Int): List[ResultLine] = {
    for( nr <- (1 to numberOfPlayers).toList) yield {
      val playerNr = (robinNr-1) * numberOfPlayers + nr
      ResultLine(SeriesPlayer(s"$playerNr","",Player(s"$playerNr",s"$robinNr $nr","",Ranks.Ng),PlayerScores( 4 - nr, nr-1, 5,3,84,60)), None)
    }
  }

  def drawHasNoSameRobinEncounters(seriesPlayers: List[SeriesPlayer], numberOfBracketRounds: Int) = {
    seriesPlayers.forall{player =>
      val robinNr = player.player.firstname.substring(0,1).toInt
      val otherPlayer = seriesPlayers.drop(Math.pow(2, numberOfBracketRounds).toInt - robinNr).head.player.firstname
     val result = otherPlayer.substring(0,1).toInt != robinNr
    if(!result) {
      println( player.player.firstname + " " + otherPlayer)
    }
    result
    }
  }
}
