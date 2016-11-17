package services

import javax.inject.Inject

import models._
import models.player.SeriesPlayerWithRoundPlayers
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository}

import scala.concurrent.{Awaitable, Future}
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesService @Inject()(seriesRoundService: SeriesRoundService, seriesPlayerRepository: SeriesPlayerRepository, seriesRepository: SeriesRepository, drawService: DrawService){
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
          case Some(foundSeries) => drawService.drawRobins(seriesPlayers, robinRound, foundSeries.setTargetScore, foundSeries.numberOfSetsToWin, drawType) match {
            case Some(robinRound) => Right(seriesId,robinRound)
            case _ => Left(DrawError(seriesId, "de reeks kon niet getrokken worden"))
          }
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

  def advanceSeries(tournamentSeries: TournamentSeries, seriesRounds: List[SeriesRound]): TournamentSeries = {
    if(seriesRounds.length > tournamentSeries.currentRoundNr) tournamentSeries.copy(currentRoundNr = tournamentSeries.currentRoundNr+1) else tournamentSeries
  }



  def calculateSeriesScores(seriesPlayersWithRoundPlayers: List[SeriesPlayerWithRoundPlayers]) = {
    seriesPlayersWithRoundPlayers.map(seriesRoundService.calculatePlayerScore)
  }
}