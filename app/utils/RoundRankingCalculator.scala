package utils

import models.player.SeriesPlayer
import models._
import models.matches.SiteMatch

object RoundRankingCalculator {



  def calculateBracketResults(bracketRound: SiteBracketRound, isWithHandicap: Boolean, scoreType: ScoreType) = {
    val updatedRound = RoundScoreCalculator.calculateBracketResults(bracketRound, isWithHandicap)
    sortPlayers(isWithHandicap, updatedRound.bracketPlayers, bracketRound.bracket.toList, scoreType)
  }

  def calculateRobinResults(robinRound: SiteRobinRound, isWithHandicap: Boolean, scoreType: ScoreType) = {
    val updatedRound = RoundScoreCalculator.calculateRobinResults(isWithHandicap, robinRound)
    sortPlayers(isWithHandicap, updatedRound.robinList.flatMap(_.robinPlayers), updatedRound.robinList.flatMap(_.robinMatches), scoreType)
  }

  def calculateRoundResults(seriesround: SeriesRound, isWithHandicap: Boolean, scoreType: ScoreType = ScoreTypes.DIRECT_CONFRONTATION): List[SeriesPlayer] =  seriesround match {
    case robinRound: SiteRobinRound => calculateRobinResults(robinRound, isWithHandicap, scoreType)
    case bracketRound: SiteBracketRound => calculateBracketResults(bracketRound, isWithHandicap, scoreType)
  }


  def sortPlayers(isWithHandicap: Boolean, playerList: List[SeriesPlayer], matchList: List[SiteMatch], scoreType: ScoreType) =
    if(scoreType == ScoreTypes.DIRECT_CONFRONTATION) {
      breakTiesWithDirectMatchup(isWithHandicap, playerList, matchList, playerList.length-1)
    }else {
      playerList.sortBy(_.playerScores.calculateScore)
    }



  def breakTiesWithDirectMatchup(isWithHandicap: Boolean, playerList: List[SeriesPlayer], matchList: List[SiteMatch], currentNumberOfWins: Int, provisionalSortedList: List[SeriesPlayer]=Nil): List[SeriesPlayer] = {

    val playersWithCurrentNumberOfWins = playerList.filter(_.playerScores.wonMatches == currentNumberOfWins)
    if(currentNumberOfWins < 0 ){
      provisionalSortedList.reverse
    } else {
      addPlayersToProvisionalSorting(isWithHandicap, playersWithCurrentNumberOfWins, playerList, matchList, currentNumberOfWins, provisionalSortedList)
    }
  }


  def bothPlayersInList(siteMatch: SiteMatch, playersWithCurrentNumberOfWins: List[SeriesPlayer]) = {
    siteMatch.playerA.exists(playersWithCurrentNumberOfWins.map(_.player).contains(_)) &&
      siteMatch.playerB.exists(playersWithCurrentNumberOfWins.map(_.player).contains(_))
  }

  def addPlayersToProvisionalSorting(isWithHandicap: Boolean, playersWithCurrentNumberOfWins: List[SeriesPlayer], playerList: List[SeriesPlayer], matchList: List[SiteMatch], currentNumberOfWins: Int, provisionalSortedList: List[SeriesPlayer]): List[SeriesPlayer] = {
    val matchesToConsider = matchList.filter(siteMatch => bothPlayersInList(siteMatch, playersWithCurrentNumberOfWins))

    playersWithCurrentNumberOfWins match {
      case Nil                                              => breakTiesWithDirectMatchup(isWithHandicap, playerList, matchList, currentNumberOfWins - 1, provisionalSortedList)
      case singlePlayerList if singlePlayerList.length == 1 => breakTiesWithDirectMatchup(isWithHandicap, playerList, matchList, currentNumberOfWins - 1, playersWithCurrentNumberOfWins.head :: provisionalSortedList)
      case _                                                =>

        val tieOrder = playersWithCurrentNumberOfWins
            .map(  seriesPlayer => (seriesPlayer,RoundScoreCalculator.getScoresOfPlayer(isWithHandicap, matchesToConsider,seriesPlayer).calculateScore) )
            .sortBy( _._2)


        breakTiesWithDirectMatchup(isWithHandicap, playerList, matchList, currentNumberOfWins - 1, tieOrder.map(_._1) ::: provisionalSortedList)
    }
  }

}
