package utils

import models.matches.{MatchChecker, PingpongMatch}
import models.player.{Player, PlayerScores}

object PingpongMatchUtils {

  def getMatchPlayers(pingpongMatch: PingpongMatch): List[Player] =
    pingpongMatch.playerA.toList ++ pingpongMatch.playerB.toList

  def bothPlayersInList(playersToMatch: List[Player]): PingpongMatch => Boolean = pingpongMatch => {
    pingpongMatch.playerA.forall(realA => playersToMatch.contains(realA)) &&
      pingpongMatch.playerB.forall(realB => playersToMatch.contains(realB))
  }

  def playerAPlayerScores(pingpongMatch: PingpongMatch): PlayerScores = {
    val calculatedMatch = MatchChecker.calculateSets(pingpongMatch)
    val wonPointsA = calculatedMatch.games.foldLeft(0)((acc, game) => acc + game.pointA)
    val wonPointsB = calculatedMatch.games.foldLeft(0)((acc, game) => acc + game.pointB)
    if (calculatedMatch.wonSetsA > calculatedMatch.wonSetsB && MatchChecker.isWon(calculatedMatch)) {
      PlayerScores(1, 0, calculatedMatch.wonSetsA, calculatedMatch.wonSetsB, wonPointsA, wonPointsB, 0)
    } else {
      PlayerScores(0, 1, calculatedMatch.wonSetsA, calculatedMatch.wonSetsB, wonPointsA, wonPointsB, 0)
    }
  }


  def playerBPlayerScores(pingpongMatch: PingpongMatch): PlayerScores = {
    val calculatedMatch = MatchChecker.calculateSets(pingpongMatch)
    val wonPointsA = calculatedMatch.games.foldLeft(0)((acc, game) => acc + game.pointA)
    val wonPointsB = calculatedMatch.games.foldLeft(0)((acc, game) => acc + game.pointB)
    if (calculatedMatch.wonSetsA < calculatedMatch.wonSetsB && MatchChecker.isWon(calculatedMatch)) {
      PlayerScores(1, 0, calculatedMatch.wonSetsB, calculatedMatch.wonSetsA, wonPointsB, wonPointsA, 0)
    } else {
      PlayerScores(0, 1, calculatedMatch.wonSetsB, calculatedMatch.wonSetsA, wonPointsB, wonPointsA, 0)
    }
  }
}
