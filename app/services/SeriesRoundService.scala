package services

import javax.inject.Inject

import models._
import models.matches.{SiteMatch, SiteMatchWithGames}
import models.player.{PlayerScores, SeriesPlayer, SeriesPlayerWithRoundPlayers, SeriesRoundPlayer}
import repositories.mongo.SeriesRoundRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundService @Inject()(matchService: MatchService, seriesRoundRepository: SeriesRoundRepository) {
/*
  def saveBracket(bracket: Bracket): Future[Option[SeriesRoundWithPlayersAndMatches]] = {

    //todo delete existing elements in DB !!!! (need roundNR???)

     roundPlayersRepository.saveBracketPlayers(bracket.bracketPlayers).map { playerList =>
       //todo save round
       // save roundMatches
        //todo save matches
        Some(bracket)
     }

  }*/

/*
  def saveRobin(robin: RobinRound): Future[Option[SeriesRoundWithPlayersAndMatches]] = {
    val players = robin.robinList.flatMap(_.robinPlayers)
    //todo delete existing elements in DB !!!! (need roundNR???)

    roundPlayersRepository.saveRobinPlayers(players).map { playerList =>
      //todo save round
      // save roundMatch
      //todo save matches
      Some(robin)
    }
  }*/

/*
  def saveFullSeriesRound(fullSeriesRound: SeriesRoundWithPlayersAndMatches): Future[Option[SeriesRoundWithPlayersAndMatches]] = fullSeriesRound match {
    case bracket: Bracket => saveBracket(bracket)
    case robin: RobinRound => saveRobin(robin)
  }*/

  def createSeriesRound(seriesRound: SeriesRound): Future[SeriesRound] = seriesRoundRepository.create(seriesRound)


  def convertRoundToGeneric(seriesRound: SeriesRound): GenericSeriesRound = seriesRound match {
    case robin  : SiteRobinRound   => GenericSeriesRound(robin.id,None, Some(robin.numberOfRobinGroups), "R",robin.seriesId, robin.roundNr)
    case bracket: SiteBracketRound => GenericSeriesRound(bracket.id, Some(bracket.numberOfBracketRounds), None, "B", bracket.seriesId, bracket.roundNr)
  }

  def convertGenericToRound(genericSeriesRound: GenericSeriesRound): SeriesRound ={
    genericSeriesRound.roundType match{
      case "R" => SiteRobinRound(genericSeriesRound.seriesRoundId, genericSeriesRound.numberOfRobins.get, genericSeriesRound.seriesId, genericSeriesRound.roundNr)
      case "B" => SiteBracketRound(genericSeriesRound.seriesRoundId, genericSeriesRound.numberOfBracketRounds.get, genericSeriesRound.seriesId, genericSeriesRound.roundNr)
    }
  }

  def updateSeriesRound(seriesRound: SeriesRound) = seriesRoundRepository.update(seriesRound)


  def getRoundsOfSeries(seriesId: String) = seriesRoundRepository.retrieveAllByField("SERIES_ID", seriesId)
  def delete(seriesRoundId: String) = seriesRoundRepository.delete(seriesRoundId)

  def calculatePlayerScore: (SeriesPlayerWithRoundPlayers) => SeriesPlayer = {
    seriesPlayerWithRoundPlayers =>
      seriesPlayerWithRoundPlayers.seriesPlayer.copy(playerScores = seriesPlayerWithRoundPlayers.seriesRoundPlayers.map(_.playerScores).foldLeft(PlayerScores())(
        (accPlayerScore: PlayerScores, seriesRoundPlayerScore: PlayerScores) =>
          PlayerScores(
            accPlayerScore.wonMatches + seriesRoundPlayerScore.wonMatches,
            accPlayerScore.lostMatches + seriesRoundPlayerScore.lostMatches,
            accPlayerScore.wonSets + seriesRoundPlayerScore.wonSets,
            accPlayerScore.lostSets + seriesRoundPlayerScore.lostSets,
            accPlayerScore.wonPoints + seriesRoundPlayerScore.wonPoints,
            accPlayerScore.lostPoints + seriesRoundPlayerScore.lostPoints,
            accPlayerScore.totalPoints + seriesRoundPlayerScore.totalPoints)
      ))
  }

  def updateSeriesRoundPlayerAfterMatch(seriesRoundPlayer: SeriesRoundPlayer, playerMatches: List[SiteMatch]) = {

    val newPlayerScore = calculateRoundPlayerScore(seriesRoundPlayer, playerMatches)
    seriesRoundPlayer.copy(playerScores = newPlayerScore)
  }

  def calculateRoundPlayerScore(seriesRoundPlayer: SeriesRoundPlayer, playerMatches: List[SiteMatch]): PlayerScores = {
    def PlayerScoresForA(acc: PlayerScores, siteMatch: SiteMatch): PlayerScores = {
      val matchWonPoints: Int = siteMatch.games.map(_.pointA).sum
      val matchLostPoints: Int = siteMatch.games.map(_.pointB).sum
      PlayerScores(
        acc.wonMatches + isMatchWonForA(siteMatch),
        acc.lostMatches + isMatchWonForB(siteMatch),
        acc.wonSets + siteMatch.wonSetsA,
        acc.lostSets + siteMatch.wonSetsB,
        acc.wonPoints + matchWonPoints,
        acc.lostPoints + matchLostPoints,
        acc.totalPoints + 10000 * isMatchWonForA(siteMatch) + 100 * siteMatch.wonSetsA / (if (siteMatch.wonSetsB == 0) 1 else siteMatch.wonSetsB) + matchWonPoints / (if (matchLostPoints == 0) 1 else matchLostPoints)
      )
    }

    def PlayerScoresForB(acc: PlayerScores, siteMatch: SiteMatch): PlayerScores = {
      val matchWonPoints: Int = siteMatch.games.map(_.pointB).sum
      val matchLostPoints: Int = siteMatch.games.map(_.pointA).sum
      PlayerScores(
        acc.wonMatches + isMatchWonForB(siteMatch),
        acc.lostMatches + isMatchWonForA(siteMatch),
        acc.wonSets + siteMatch.wonSetsB,
        acc.lostSets + siteMatch.wonSetsA,
        acc.wonPoints + matchWonPoints,
        acc.lostPoints + matchLostPoints,
        acc.totalPoints + 10000 * isMatchWonForB(siteMatch) + 100 * siteMatch.wonSetsB / (if (siteMatch.wonSetsA == 0) 1 else siteMatch.wonSetsA) + siteMatch.games.map(_.pointB).sum / (if (matchLostPoints == 0) 1 else matchLostPoints)
      )
    }

    playerMatches.foldLeft(PlayerScores()) { (acc, siteMatch) =>
      if (seriesRoundPlayer.id == siteMatch.playerA.map(_.id).getOrElse("0")) {
        PlayerScoresForA(acc, siteMatch)
      } else if (seriesRoundPlayer.id == siteMatch.playerB.map(_.id).getOrElse("0")) {
        PlayerScoresForB(acc, siteMatch)
      } else {
        acc
      }
    }
  }

  def isMatchWonForA(siteMatch: SiteMatch): Int = {
    if (siteMatch.wonSetsA > siteMatch.wonSetsB && siteMatch.wonSetsA == siteMatch.numberOfSetsToWin) 1 else 0
  }

  def isMatchWonForB(siteMatch: SiteMatch): Int = {
    if (siteMatch.wonSetsB > siteMatch.wonSetsA && siteMatch.wonSetsB == siteMatch.numberOfSetsToWin) 1 else 0
  }


  val seriesRounds: List[SeriesRound] = List(
    SiteRobinRound("1", 2, "1", 1),
    SiteBracketRound("2", 2, "1", 1)
  )

  def getSeriesRound(seriesRoundId: String) = {
    Future(seriesRounds.find(round => round.id.contains(seriesRoundId)))
  }


  def isRoundComplete(seriesRoundWithPlayersAndMatches: SeriesRoundWithPlayersAndMatches): Boolean = seriesRoundWithPlayersAndMatches match {
    case robinRound: RobinRound =>
      robinRound.robinList.forall(robinGroup => robinGroup.robinMatches.forall(matchService.isMatchComplete))
    case bracketRound: Bracket =>
      bracketRound.bracketRounds.forall(bracketMatchList => bracketMatchList.forall(matchService.isBracketMatchComplete))
  }
}
