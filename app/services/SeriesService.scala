package services

import javax.inject.Inject

import models._
import models.player.{SeriesPlayer, SeriesPlayerWithRoundPlayers}
import play.api.libs.json.Json
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesService @Inject()(seriesRoundService: SeriesRoundService, seriesPlayerRepository: SeriesPlayerRepository, seriesRepository: SeriesRepository, drawService: DrawService){
  type FinalRanking = List[SeriesPlayer]

  def create(tournamentSeries: TournamentSeries) = seriesRepository.create(tournamentSeries)
  def update(tournamentSeries: TournamentSeries) = seriesRepository.update(tournamentSeries)
  def delete(seriesId: String) = seriesRepository.delete(seriesId)

  def retrieveById(seriesId: String) = seriesRepository.retrieveById(seriesId)
  def retrieveAllByField(fieldKey: String, fieldValue: String) = seriesRepository.retrieveAllByField(fieldKey, fieldValue)
  def getTournamentSeriesOfTournament(tournamentId: String): Future[List[TournamentSeries]] = seriesRepository.retrieveAllByField("tournamentId", tournamentId)


  def drawTournamentFirstRounds(tournamentId: String): Future[List[Either[DrawError, (String, SeriesRound)]]] = {
    getStartingRoundsOfTournament(tournamentId).flatMap { roundList =>
      Future.sequence {
        roundList.map(round => drawRound(round))
      }
    }
  }


  def getStartingRoundsOfTournament(tournamentId: String): Future[List[SeriesRound]] = seriesRepository.retrieveAllByField("tournamentId", tournamentId).flatMap { seriesList =>
    Future.sequence {
      seriesList.map {
        series => seriesRoundService.getRoundsOfSeries(series.id).map(_.sortBy(_.roundNr).headOption)
      }
    }
  }.map(_.flatten)

  def drawRound(round: SeriesRound): Future[Either[DrawError, (String, SeriesRound)]] = round match {
    case robinRound: SiteRobinRound => drawRobinRound(robinRound.id, robinRound.seriesId, robinRound)
    case bracketRound: SiteBracketRound => drawBracketRound(bracketRound.id, bracketRound.seriesId, bracketRound)
  }

  def drawRobinRound(roundId: String, seriesId: String, robinRound: SiteRobinRound, drawType: DrawType = DrawTypes.RankedRandomOrder): Future[Either[DrawError, (String, SiteRobinRound)]] = {
    seriesPlayerRepository.retrieveAllSeriesPlayers(seriesId).flatMap { seriesPlayers =>
      if (seriesPlayers.length > 1) {
        seriesRepository.retrieveById(seriesId).map {
          case Some(foundSeries) =>
                    val drawResult = drawService.drawRobins(seriesPlayers, robinRound, foundSeries.setTargetScore, foundSeries.numberOfSetsToWin, drawType)
                    drawnRoundOrError(seriesId)(drawResult)
          case _ => Left(DrawError(seriesId, "Reeks niet gevonden!"))
        }
      } else {
        Future(Left(DrawError(seriesId, s"De reeks $seriesId moet minstens twee spelers bevatten!")))
      }
    }
  }

  def drawBracketRound(roundId: String, seriesId: String, bracket: SiteBracketRound): Future[Either[DrawError, (String, SiteBracketRound)]] = {
    seriesPlayerRepository.retrieveAllSeriesPlayers(seriesId).flatMap { seriesPlayers =>
      if (seriesPlayers.length > 1) {
        seriesRepository.retrieveById(seriesId).map {
          case Some(foundSeries) => drawService.drawBracket(seriesPlayers, bracket, foundSeries.numberOfSetsToWin, foundSeries.setTargetScore) match {
            case Some(round) => Right(seriesId, round)
            case _ => Left(DrawError(seriesId, "de reeks kon niet getrokken worden"))
          }
          case _ => Left(DrawError(seriesId, "Reeks niet gevonden!"))
        }
      } else {
        Future(Left(DrawError(seriesId,s"De reeks $seriesId moet minstens twee spelers bevatten!")))
      }
    }
  }

  def calculateSeriesScores(seriesPlayersWithRoundPlayers: List[SeriesPlayerWithRoundPlayers]) = {
    seriesPlayersWithRoundPlayers.map(seriesRoundService.calculatePlayerScore)
  }


  def advanceIfPossibleOrShowFinalRanking(series: TournamentSeries, roundRanking: List[SeriesPlayer]): Future[Either[FinalRanking, SeriesRound]] = {
    seriesRoundService
      .retrieveByFields(Json.obj("roundNr" -> (series.currentRoundNr + 1), "seriesId" -> series.id))
      .flatMap(returnRoundRankingOrNextRoundIfPresent(series, roundRanking))
  }

  def returnRoundRankingOrNextRoundIfPresent(series: TournamentSeries, roundRanking: FinalRanking): Option[SeriesRound] => Future[Either[FinalRanking, SeriesRound]] = {
    case Some(nextRound) => update(series.copy(currentRoundNr = series.currentRoundNr + 1)).flatMap{ _ =>

      val updatedRound = drawService.drawSubsequentRound(nextRound, roundRanking, series).getOrElse(nextRound)
      seriesRoundService.updateSeriesRound(updatedRound).map( _  => Right(updatedRound))
    }
    case _ => Future(Left(roundRanking))
  }

  def drawnRoundOrError(seriesId: String): Option[SiteRobinRound] => Either[DrawError, (String, SiteRobinRound)] = robinOpt => robinOpt match {
    case Some(drawnRobinRound) => Right(seriesId,drawnRobinRound)
    case _ => Left(DrawError(seriesId, "de reeks kon niet getrokken worden"))
  }

}