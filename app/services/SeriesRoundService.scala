package services

import javax.inject.Inject

import models._
import models.matches.{MatchChecker, PingpongMatch}
import models.player.{PlayerScores, SeriesPlayer, SeriesPlayerWithRoundPlayers}
import play.api.libs.json.{JsObject, Json}
import repositories.mongo.{SeriesRepository, SeriesRoundRepository}
import utils.RoundScorer

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundService @Inject()(matchService: MatchService, seriesRoundRepository: SeriesRoundRepository, seriesRepository: SeriesRepository) {
  def retrieveAllByField(fieldKey: String, fieldValue: String): Future[List[SeriesRound]] = seriesRoundRepository.retrieveAllByField(fieldKey, fieldValue)
  def retrieveByFields(jsObject: JsObject): Future[Option[SeriesRound]] = seriesRoundRepository.retrieveByFields(jsObject)

  def getMatchesOfRound(seriesRoundId: String): Future[List[PingpongMatch]] = seriesRoundRepository.retrieveById(seriesRoundId).map{
    case Some(bracketRound: SiteBracketRound) => bracketRound.bracket.toList
    case Some(robinRound: SiteRobinRound) => robinRound.matches
    case Some(swissRound: SwissRound) => swissRound.matches
    case None => List()
  }

  def getRound(roundId: String): Future[Option[SeriesRound]] = seriesRoundRepository.retrieveById(roundId)


  def calculateRoundResults(playingWithHandicaps: Boolean, seriesRound: SeriesRound): SeriesRound = seriesRound match{
    case robinRound: SiteRobinRound => RoundScorer.calculateRobinResults(playingWithHandicaps, robinRound)
    case bracketRound: SiteBracketRound => RoundScorer.calculateBracketResults(bracketRound, playingWithHandicaps)
  }

  def updateMatchInRound(siteMatch: PingpongMatch, round: SeriesRound): SeriesRound = round match{
    case robinRound: SiteRobinRound => robinRound.copy(robinList = robinRound.robinList.map(group => group.copy( robinMatches = group.robinMatches.map(matchToUpdateOrOriginalMatch(siteMatch)))))
    case bracketRound: SiteBracketRound => bracketRound.copy(bracket = bracketRound.bracket.map(matchToUpdateOrOriginalMatch(siteMatch)))
  }

  def updateHallMatch(pingpongMatch: PingpongMatch): Future[Option[SeriesRound]] = retrieveByFields(Json.obj("id" -> pingpongMatch.roundId)).map{
    _.map{ round =>
      val updatedRound = calculateRoundResults(pingpongMatch.handicap == 0, updateMatchInRound(MatchChecker.calculateSets(pingpongMatch), round))
      seriesRoundRepository.update(updatedRound)
      updatedRound
    }
  }

  def matchToUpdateOrOriginalMatch(siteMatch: PingpongMatch): (PingpongMatch) => PingpongMatch = {
    matchTocheck => if (matchTocheck.id == siteMatch.id) siteMatch else matchTocheck
  }

  def updateRoundWithMatch(siteMatch: PingpongMatch, roundId: String): Future[Option[SeriesRound]] = {
    seriesRoundRepository.retrieveById(roundId).flatMap{
      case Some(round) =>
        seriesRepository.retrieveById(round.seriesId).flatMap{
          case Some(series) =>
            val updatedRound = calculateRoundResults(series.playingWithHandicaps, updateMatchInRound(siteMatch, round))
            updateSeriesRound(updatedRound).map{ _ => Some(updatedRound)
          }
          case _ => Future(None)
        }
      case _ => Future(None)
    }
  }


  def createSeriesRound(seriesRound: SeriesRound): Future[SeriesRound] = {
    def getNextRoundNrOfSeries(seriesId: String) = {
      seriesRoundRepository.retrieveAllByField("seriesId",seriesId).map( list => seriesRound match{
        case r: SiteRobinRound => r.copy(roundNr = list.length + 1 )
        case b: SiteBracketRound => b.copy(roundNr = list.length + 1)
        case s: SwissRound => s.copy(roundNr = list.length + 1)
      })
    }
    val seriesToCreate = getNextRoundNrOfSeries(seriesRound.seriesId)
    seriesToCreate.flatMap(seriesRoundRepository.create)
  }


  def updateSeriesRound(seriesRound: SeriesRound): Future[SeriesRound] = seriesRoundRepository.update(seriesRound)


  def getRoundsOfSeries(seriesId: String): Future[List[SeriesRound]] = seriesRoundRepository.retrieveAllByField("seriesId", seriesId)


  def updateRoundNrForDeletedNr(roundNrToDelete: Int): (SeriesRound) => Future[SeriesRound] = {
    def decreaseRoundNr(round: SeriesRound) = round match {
      case r:SiteRobinRound => r.copy(roundNr = r.roundNr - 1)
      case b:SiteBracketRound => b.copy(roundNr = b.roundNr - 1)
      case s:SwissRound => s.copy(roundNr = s.roundNr - 1)
    }

    round => if(round.roundNr > roundNrToDelete) updateSeriesRound(decreaseRoundNr(round)) else Future(round)
  }

  def updateRoundNrs(round: SeriesRound): Future[List[SeriesRound]] = {
    val roundToDeleteNr = round.roundNr
    getRoundsOfSeries(round.seriesId).flatMap{ rounds => Future.sequence{
          rounds.map(updateRoundNrForDeletedNr(roundToDeleteNr))
        }
       }

  }

  def delete(seriesRoundId: String): Future[Unit] = {
    seriesRoundRepository.retrieveById(seriesRoundId).flatMap {
      case Some(round) => updateRoundNrs(round).flatMap(updated =>
        seriesRoundRepository.delete(seriesRoundId)).map(_ => Some(()))
      case None => Future(None)
    }
    seriesRoundRepository.delete(seriesRoundId)
  }

  def calculatePlayerScore: (SeriesPlayerWithRoundPlayers) => SeriesPlayer = {
    seriesPlayerWithRoundPlayers =>
      seriesPlayerWithRoundPlayers.seriesPlayer.copy(playerScores = seriesPlayerWithRoundPlayers.seriesRoundPlayers.map(_.playerScores).foldLeft(PlayerScores())(
        (accPlayerScore: PlayerScores, seriesRoundPlayerScore: PlayerScores) => accPlayerScore + seriesRoundPlayerScore
      ))
  }

  def updateSeriesPlayerAfterMatch(seriesPlayer: SeriesPlayer, playerMatches: List[PingpongMatch]): SeriesPlayer = {

    val newPlayerScore = calculateRoundPlayerScore(seriesPlayer, playerMatches)
    seriesPlayer.copy(playerScores = newPlayerScore)
  }

  def calculateRoundPlayerScore(seriesRoundPlayer: SeriesPlayer, playerMatches: List[PingpongMatch]): PlayerScores = {
    def PlayerScoresForA(acc: PlayerScores, siteMatch: PingpongMatch): PlayerScores = {
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

    def PlayerScoresForB(acc: PlayerScores, siteMatch: PingpongMatch): PlayerScores = {
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

  def isMatchWonForA(siteMatch: PingpongMatch): Int = {
    if (siteMatch.wonSetsA > siteMatch.wonSetsB && siteMatch.wonSetsA == siteMatch.numberOfSetsToWin) 1 else 0
  }

  def isMatchWonForB(siteMatch: PingpongMatch): Int = {
    if (siteMatch.wonSetsB > siteMatch.wonSetsA && siteMatch.wonSetsB == siteMatch.numberOfSetsToWin) 1 else 0
  }


  def isRoundComplete(seriesRound: SeriesRound): Boolean = seriesRound match {
    case robinRound: SiteRobinRound =>
      robinRound.robinList.forall(robinGroup => robinGroup.robinMatches.forall(matchService.isMatchComplete))
    case bracketRound: SiteBracketRound =>
      SiteBracket.isComplete(bracketRound.bracket)
    case swissRound: SwissRound =>
      swissRound.matches.forall(matchService.isMatchComplete)
  }
}
