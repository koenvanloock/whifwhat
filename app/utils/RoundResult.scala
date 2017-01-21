package utils

import models.matches.SiteMatch
import models.player.{PlayerScores, SeriesPlayer}
import models._

object RoundResult {
  def calculateBracketResults(bracketRound: SiteBracketRound): SiteBracketRound = bracketRound.copy(bracket = SiteBracket.updateMatchWithChildrenWinner(bracketRound.bracket))

  def dealWithHandicap(matchToAdd: SiteMatch, aOrB: String) = if(matchToAdd.isHandicapForB && aOrB == "B"){
    matchToAdd.handicap
  }else if(!matchToAdd.isHandicapForB && aOrB == "A"){
    matchToAdd.handicap
  }else{
    0
  }

  def addPlayerAscores(matchToAdd: SiteMatch, acc: PlayerScores): PlayerScores = acc.copy(
    wonMatches = acc.wonMatches + (if(matchToAdd.wonSetsA == matchToAdd.numberOfSetsToWin) 1 else 0),
    lostMatches = acc.lostMatches + (if(matchToAdd.wonSetsB == matchToAdd.numberOfSetsToWin) 1 else 0),
    wonSets = acc.wonSets +  matchToAdd.wonSetsA,
    lostSets = acc.lostSets + matchToAdd.wonSetsB,
    wonPoints = acc.wonPoints + matchToAdd.games.foldRight(0)( (game, pointsOfA) => pointsOfA + game.pointA - dealWithHandicap(matchToAdd, "A")),
    lostPoints = acc.lostPoints + matchToAdd.games.foldRight(0)( (game, pointsOfB) => pointsOfB + game.pointB - dealWithHandicap(matchToAdd, "B"))
  )

  def addPlayerBscores(matchToAdd: SiteMatch, acc: PlayerScores): PlayerScores = acc.copy(
    wonMatches = acc.wonMatches + (if(matchToAdd.wonSetsB == matchToAdd.numberOfSetsToWin) 1 else 0),
    lostMatches = acc.lostMatches + (if(matchToAdd.wonSetsA == matchToAdd.numberOfSetsToWin) 1 else 0),
    wonSets = acc.wonSets +  matchToAdd.wonSetsB,
    lostSets = acc.lostSets + matchToAdd.wonSetsA,
    wonPoints = acc.wonPoints + matchToAdd.games.foldRight(0)( (game, pointsOfB) => pointsOfB + game.pointB - dealWithHandicap(matchToAdd, "B")),
    lostPoints = acc.lostPoints + matchToAdd.games.foldRight(0)( (game, pointsOfA) => pointsOfA + game.pointA - dealWithHandicap(matchToAdd, "A"))
  )

  def getScoresOfPlayer(matchList: List[SiteMatch], seriesPlayer: SeriesPlayer): PlayerScores = matchList.foldRight[PlayerScores](PlayerScores()){ (matchToAdd, acc) =>
      if(matchToAdd.playerA.exists(player => player.id == seriesPlayer.player.id)){
        println("the player was A")
        addPlayerAscores(matchToAdd, acc)
      } else if(matchToAdd.playerB.exists(player => player.id == seriesPlayer.player.id)){
        println("the player was B")
        addPlayerBscores(matchToAdd, acc)
      } else{
        println("the player wasn't found")
        acc
      }
  }

  def calculatePlayerScore(matchList: List[SiteMatch]): (SeriesPlayer) => SeriesPlayer = seriesPlayer => seriesPlayer.copy(playerScores = {println("scores: "+ getScoresOfPlayer(matchList, seriesPlayer));getScoresOfPlayer(matchList, seriesPlayer)})

  // todo perhaps deal with direct confrontations, calculate totals
  def calculateRobinGroupScores(robinGroup: RobinGroup): RobinGroup = robinGroup.copy(robinPlayers = robinGroup.robinPlayers.map(calculatePlayerScore(robinGroup.robinMatches)))

  def calculateRobinResults(robinRound: SiteRobinRound): SiteRobinRound = robinRound.copy(robinList = robinRound.robinList.map{ robinGroup => calculateRobinGroupScores(robinGroup)})


}
