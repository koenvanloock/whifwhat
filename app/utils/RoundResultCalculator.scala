package utils

import models.{ResultLine, RobinGroup, SeriesRound, SiteRobinRound}

object RoundResultCalculator {
  def calculateResult(seriesRound: SeriesRound, isHandicap: Boolean) = {
    case robin:SiteRobinRound => calculateRobinResult(robin, isHandicap)
  }

  def calculateRobinResult(robinRound: SiteRobinRound, isWithHandicap: Boolean) = {
    robinRound.robinList.map(getResultLinesOfRobinGroup(isWithHandicap))
  }

  def getResultLinesOfRobinGroup(isWithHandicap: Boolean): RobinGroup => List[ResultLine] = robinGroup => {
    val updatedSeriesPlayers = robinGroup.robinPlayers.map{ seriesPlayer =>RoundScorer.calculatePlayerScore(isWithHandicap, robinGroup.robinMatches)(seriesPlayer)}

    calculateTieScoreAndGenerateResultLine(updatedSeriesPlayers, robinGroup.robinMatches)


  }
}
