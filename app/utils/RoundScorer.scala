package utils

import models.matches.PingpongMatch
import models.player.{PlayerScores, SeriesPlayer}
import models._
import models.types.Bracket

object RoundScorer {

  def calculatePlayerResults(isWithHandicap: Boolean, bracketPlayers: List[SeriesPlayer], updatedBracket: Bracket[PingpongMatch]): List[SeriesPlayer] = {
    bracketPlayers.map(calculatePlayerScores(updatedBracket, isWithHandicap))
  }

  def calculatePlayerScores(updatedBracket: Bracket[PingpongMatch], isWithHandicap: Boolean): (SeriesPlayer) => SeriesPlayer = { seriesPlayer =>
    seriesPlayer.copy(playerScores = updatedBracket.fold(getScoresOfMatch(isWithHandicap,seriesPlayer))(_ + _))
  }

  def getScoresOfMatch(isWithHandicap: Boolean, playerToCheck: SeriesPlayer): (PingpongMatch) => PlayerScores = siteMatch =>
    if(siteMatch.playerA.contains(playerToCheck.player)) {
      addPlayerAscores(isWithHandicap, siteMatch)
    }else if(siteMatch.playerB.contains(playerToCheck.player)) {
      addPlayerBscores(isWithHandicap, siteMatch)
    } else {
      PlayerScores()
    }


  def calculateBracketResults(bracketRound: SiteBracketRound, isWithHandicap: Boolean): SiteBracketRound = {
    val updatedBracket = SiteBracket.updateMatchWithChildrenWinner(bracketRound.bracket)
    val updatedPlayers = applyScoreKey(calculatePlayerResults(isWithHandicap, bracketRound.bracketPlayers, updatedBracket), bracketRound.scoreKey)
    bracketRound.copy(bracket = updatedBracket, bracketPlayers = updatedPlayers)
  }


  def addPlayerAscores(isWithHandicap: Boolean, matchToAdd: PingpongMatch, acc: PlayerScores = PlayerScores()): PlayerScores = acc.copy(
    wonMatches = acc.wonMatches + (if(matchToAdd.wonSetsA == matchToAdd.numberOfSetsToWin) 1 else 0),
    lostMatches = acc.lostMatches + (if(matchToAdd.wonSetsB == matchToAdd.numberOfSetsToWin) 1 else 0),
    wonSets = acc.wonSets +  matchToAdd.wonSetsA,
    lostSets = acc.lostSets + matchToAdd.wonSetsB,
    wonPoints = acc.wonPoints + matchToAdd.games.foldRight(0)( (game, pointsOfA) => pointsOfA + game.pointA),
    lostPoints = acc.lostPoints + matchToAdd.games.foldRight(0)( (game, pointsOfB) => pointsOfB + game.pointB)
  )

  def addPlayerBscores(isWithHandicap: Boolean, matchToAdd: PingpongMatch, acc: PlayerScores = PlayerScores()): PlayerScores = acc.copy(
    wonMatches = acc.wonMatches + (if(matchToAdd.wonSetsB == matchToAdd.numberOfSetsToWin) 1 else 0),
    lostMatches = acc.lostMatches + (if(matchToAdd.wonSetsA == matchToAdd.numberOfSetsToWin) 1 else 0),
    wonSets = acc.wonSets +  matchToAdd.wonSetsB,
    lostSets = acc.lostSets + matchToAdd.wonSetsA,
    wonPoints = acc.wonPoints + matchToAdd.games.foldRight(0)( (game, pointsOfB) => pointsOfB + game.pointB),
    lostPoints = acc.lostPoints + matchToAdd.games.foldRight(0)( (game, pointsOfA) => pointsOfA + game.pointA)
  )

  def getScoresOfPlayer(isWithHandicap: Boolean, matchList: List[PingpongMatch], seriesPlayer: SeriesPlayer): PlayerScores = matchList.foldRight[PlayerScores](PlayerScores()){ (matchToAdd, acc) =>
      if(matchToAdd.playerA.exists(player => player.id == seriesPlayer.player.id)){
        addPlayerAscores(isWithHandicap, matchToAdd, acc)
      } else if(matchToAdd.playerB.exists(player => player.id == seriesPlayer.player.id)){
        addPlayerBscores(isWithHandicap, matchToAdd, acc)
      } else{
        acc
      }
  }

  def calculatePlayerScore(isWithHandicap: Boolean, matchList: List[PingpongMatch]): (SeriesPlayer) => SeriesPlayer = seriesPlayer => seriesPlayer.copy(playerScores = getScoresOfPlayer(isWithHandicap, matchList, seriesPlayer))

  def applyScoreKey(robinGroupPlayers: List[SeriesPlayer], scoreKeyOpt: Option[List[Int]]): List[SeriesPlayer] =  scoreKeyOpt match{
    case Some(scoreKey) => robinGroupPlayers.zip(scoreKey).map{case (seriesPlayer, indexScore) => seriesPlayer.copy(playerScores = seriesPlayer.playerScores.copy(totalPoints = indexScore))}
    case _ => robinGroupPlayers.map(player => player.copy(playerScores = player.playerScores.copy(totalPoints = player.playerScores.calculateScore.toInt)))
  }

  def calculateRobinGroupScores(isWithHandicap: Boolean, scoreKeyOpt: Option[List[Int]], robinGroup: RobinGroup): RobinGroup = robinGroup.copy(robinPlayers = applyScoreKey(robinGroup.robinPlayers.map(calculatePlayerScore(isWithHandicap, robinGroup.robinMatches)),scoreKeyOpt))

  def calculateRobinResults(isWithHandicap: Boolean, robinRound: SiteRobinRound): SiteRobinRound = robinRound.copy(robinList = robinRound.robinList.map{ robinGroup => calculateRobinGroupScores(isWithHandicap, robinRound.scoreKey, robinGroup)})


}