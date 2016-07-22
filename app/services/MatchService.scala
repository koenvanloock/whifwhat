package services

import models.matches.{SiteGame, BracketMatchWithGames, SiteMatchWithGames}

class MatchService {
  def isMatchComplete(siteMatchWithGames: SiteMatchWithGames): Boolean = siteMatchWithGames.sets.forall(_.isCorrect(siteMatchWithGames.targetScore))
  def isBracketMatchComplete(bracketMatchWithGames: BracketMatchWithGames): Boolean = bracketMatchWithGames.sets.forall(_.isCorrect(bracketMatchWithGames.targetScore))

  def calculateMatchStats(siteMatch: SiteMatchWithGames) = {
    siteMatch.copy(
      wonSetsA = calculateSetsForPlayer(siteMatch, 'A'),
      wonSetsB = calculateSetsForPlayer(siteMatch, 'B')
    )

  }

  def isForA(targetScore: Int): (SiteGame) => Boolean = game => game.isCorrect(targetScore) && game.pointA > game.pointB
  def isForB(targetScore: Int): (SiteGame) => Boolean = game => game.isCorrect(targetScore) && game.pointB > game.pointA



  private def calculateSetsForPlayer(siteMatchWithGames: SiteMatchWithGames, playerChar: Char): Int = {
    playerChar match{
      case 'A' => siteMatchWithGames.sets.count(isForA(siteMatchWithGames.targetScore))
      case 'B' =>siteMatchWithGames.sets.count(isForB(siteMatchWithGames.targetScore))
    }
  }
}
