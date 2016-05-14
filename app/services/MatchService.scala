package services

import models.{SiteGame, SiteMatchWithGames, SiteMatch}

/**
  * @author Koen Van Loock
  * @version 1.0 12/05/2016 20:58
  */
class MatchService {


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
