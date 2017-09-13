package utils

import models.ResultLine
import models.player.{Player, PlayerScores, Ranks, SeriesPlayer}

object BracketPlacement {

  def sortRobinPlayersForPlacedBracket(robinResult: List[List[ResultLine]], numberOfBracketRounds: Int): List[SeriesPlayer] = {
    val robinCount = robinResult.length

    robinCount match {
      case 1 => robinResult.head.map(_.player).take(Math.pow(2, numberOfBracketRounds).toInt)
      case _ => getListForSizeTwo(numberOfBracketRounds, robinResult)
    }
  }

  def getListForSizeTwo(numberOfBracketRounds: Int, robinResult: List[List[ResultLine]]): List[SeriesPlayer] = {
    val pickedList = for{
      playerNr <- (1 to Math.pow(2, numberOfBracketRounds - 1).toInt).toList
      robinNr <- (1 to robinResult.length).toList
    } yield {
      getRobinPlayer(robinResult)(robinNr, playerNr)
    }


    val numberOfSureElements = Math.pow(2, numberOfBracketRounds).toInt / robinResult.length * robinResult.length
    val sureList = pickedList.take(numberOfSureElements)
    val numberOfFillerSpots = Math.pow(2, numberOfBracketRounds).toInt % robinResult.length
    val fillers = SortUtils.sortSeriesPlayers(pickedList.slice(numberOfSureElements, numberOfSureElements + robinResult.length)).take(numberOfFillerSpots)
    sureList ++ fillers
  }

  def getRobinPlayer(robinResult: List[List[ResultLine]])(robinNumber: Int, playerNumber: Int): SeriesPlayer ={
    robinResult.drop(robinNumber - 1).headOption.flatMap( resultLineList => resultLineList.drop(playerNumber - 1).headOption.map(_.player))
      .getOrElse(SeriesPlayer("anonymous", "", Player("0","","",Ranks.Rec), PlayerScores()))
  }

}
