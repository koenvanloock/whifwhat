package utils

import java.util.UUID

import models.matches.PingpongMatch
import models.player.{PlayerScores, SeriesPlayer}
import models._


/*
*   This cass calculates the roundresults for a seriesround, dealing with robin ties by calculating the score of all tiedplayers' matches
*
* */

object RoundResultCalculator {
  def calculateResult(seriesRound: SeriesRound, isHandicap: Boolean): RoundResult = seriesRound match {
    case robin: SiteRobinRound => calculateRobinResult(robin, isHandicap)
    case bracket: SiteBracketRound => calculateBracketResult(bracket, isHandicap)
  }

  def calculateRobinResult(robinRound: SiteRobinRound, isWithHandicap: Boolean): RoundResult = {
    val robinResult = robinRound.robinList.map(getResultLinesOfRobinGroup(isWithHandicap))
    RoundResult(UUID.randomUUID().toString, robinRound.id, SortUtils.sortResultLines(ResultLineSortingTypes.GENERAL, aggregateRobins(robinResult)), Some(robinResult))
  }


  def calculateBracketResult(bracketRound: SiteBracketRound, isWithHandicap: Boolean): RoundResult = {
    val updatedBracket = RoundScorer.calculateBracketResults(bracketRound, isWithHandicap)
    RoundResult(UUID.randomUUID().toString, bracketRound.id, SortUtils.sortResultLines(ResultLineSortingTypes.GENERAL, updatedBracket.bracketPlayers.map(seriesPlayer => ResultLine(seriesPlayer, None))), None)
  }

  def getResultLinesOfRobinGroup(isWithHandicap: Boolean): RobinGroup => List[ResultLine] = robinGroup => {
    val updatedSeriesPlayers = robinGroup.robinPlayers.map { seriesPlayer => RoundScorer.calculatePlayerScore(isWithHandicap, robinGroup.robinMatches)(seriesPlayer) }

    calculateTieScoreAndGenerateResultLine(updatedSeriesPlayers, robinGroup.robinMatches)
  }

  def calculateTieScoreAndGenerateResultLine(updatedSeriesPlayers: List[SeriesPlayer], robinMatches: List[PingpongMatch]): List[ResultLine] = {
    val wonMatchGroups = updatedSeriesPlayers.groupBy(_.playerScores.wonMatches)

    val resultLines = for {
      numberOfWonMatches <- (updatedSeriesPlayers.length - 1 to 0 by -1).toList
      tieGroup <- wonMatchGroups.get(numberOfWonMatches)
    } yield {
      if (tieGroup.length == 1) tieGroup.map(player => ResultLine(player, None))
      else if (tieGroup.length > 1) breakTiesWithPlayers(tieGroup, robinMatches)
      else Nil
    }
    SortUtils.sortResultLines(ResultLineSortingTypes.GENERAL, resultLines.flatten)
  }


  def breakTiesWithPlayers(tiedPlayers: List[SeriesPlayer], robinMatches: List[PingpongMatch]): List[ResultLine] = {
    val resultLines = tiedPlayers.map { tiedPlayer =>
      val tiedScore = calculateTieScoreFor(tiedPlayers, robinMatches.filter(PingpongMatchUtils.bothPlayersInList(tiedPlayers.map(_.player))))(tiedPlayer)
      ResultLine(tiedPlayer, Some(tiedScore))
    }

    SortUtils.sortResultLines(ResultLineSortingTypes.TIE_SORTING, resultLines)
  }

  def calculateTieScoreFor(tiedPlayers: List[SeriesPlayer], tiedMatches: List[PingpongMatch]): SeriesPlayer => PlayerScores = tiedPlayer => {
    tiedMatches.foldLeft(PlayerScores()) {
      (acc, tiedMatch) =>
        if (tiedMatch.playerA.contains(tiedPlayer.player)) acc + PingpongMatchUtils.playerAPlayerScores(tiedMatch)
        else if (tiedMatch.playerB.contains(tiedPlayer.player)) acc + PingpongMatchUtils.playerBPlayerScores(tiedMatch)
        else acc
    }
  }

  def aggregateRobins(robinLines: List[List[ResultLine]], result: List[ResultLine] = Nil): List[ResultLine] = {
    if (robinLines.nonEmpty && robinLines.head.nonEmpty) {
      val newResult = robinLines.flatMap(_.headOption) ++ result
      aggregateRobins(robinLines.map( lineList => if(lineList.nonEmpty) lineList.tail else Nil), newResult)
    } else {
      result
    }
  }
}
