package services

import models.matches.{BracketMatch, SiteGame, SiteMatch, SiteMatchWithGames}

class MatchService {
  def isMatchComplete(siteMatch: SiteMatch): Boolean = siteMatch.games.forall(_.isCorrect(siteMatch.targetScore))
  def isBracketMatchComplete(bracketMatchWithGames: BracketMatch): Boolean = bracketMatchWithGames.siteMatch.games.forall(_.isCorrect(bracketMatchWithGames.siteMatch.targetScore))

  def calculateMatchStats(siteMatch: SiteMatch) = {
    siteMatch.copy(
      wonSetsA = calculateSetsForPlayer(siteMatch, 'A'),
      wonSetsB = calculateSetsForPlayer(siteMatch, 'B')
    )

  }

  def isForA(targetScore: Int): (SiteGame) => Boolean = game => game.isCorrect(targetScore) && game.pointA > game.pointB
  def isForB(targetScore: Int): (SiteGame) => Boolean = game => game.isCorrect(targetScore) && game.pointB > game.pointA



  private def calculateSetsForPlayer(siteMatch: SiteMatch, playerChar: Char): Int = {
    playerChar match{
      case 'A' => siteMatch.games.count(isForA(siteMatch.targetScore))
      case 'B' =>siteMatch.games.count(isForB(siteMatch.targetScore))
    }
  }
}
