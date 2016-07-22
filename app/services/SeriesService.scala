package services

import javax.inject.Inject

import models._
import models.player.{SeriesPlayerWithRoundPlayers, SeriesPlayer, Player}
import repositories.{SeriesPlayerRepository, SeriesRepository}

import scala.concurrent.{Future, Awaitable}
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesService @Inject()(seriesRoundService: SeriesRoundService, seriesPlayerRepository: SeriesPlayerRepository, seriesRepository: SeriesRepository, drawService: DrawService){
  val series=  List(
    TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")
  )

  def getTournamentSeries(seriesId: String): Future[Option[TournamentSeries]] = {
    Future(series.find(series => series.seriesId.contains(seriesId)))
  }
  def getTournamentSeriesOfTournament(tournamentId: String): Future[List[TournamentSeries]] = {
    Future(series.filter(series => series.seriesId.contains(tournamentId)))
  }

  def drawTournamentFirstRounds(tournamentId: String): Future[List[Either[DrawError, (String, SeriesRoundWithPlayersAndMatches)]]] = {
    getStartingRoundsOfTournament(tournamentId).flatMap { roundList =>
      Future.sequence {
        roundList.map(round => drawRound(seriesRoundService.convertGenericToRound(round)))
      }
    }
  }

  def getStartingRoundsOfTournament(tournamentId: String): Future[List[GenericSeriesRound]] = seriesRepository.retrieveAllByField("TOURNAMENT_ID", tournamentId).flatMap { seriesList =>
    Future.sequence {
      seriesList.map {
        series => seriesRoundService.getRoundsOfSeries(series.seriesId).map(_.sortBy(_.roundNr).headOption)
      }
    }
  }.map(_.flatten)

  def drawRound(round: SeriesRound): Future[Either[DrawError, (String, SeriesRoundWithPlayersAndMatches)]] = round match {
    case robinRound: SiteRobinRound => drawRobinRound(robinRound.getId, robinRound.seriesId, robinRound.numberOfRobinGroups)
    case bracketRound: SiteBracketRound => drawBracketRound(bracketRound.getId, bracketRound.seriesId, bracketRound.numberOfBracketRounds)
  }

  def drawRobinRound(roundId: String, seriesId: String, numberOfRobinGroups: Int, drawType: DrawType = DrawTypes.RankedRandomOrder): Future[Either[DrawError, (String, RobinRound)]] = {
    seriesPlayerRepository.retrieveAllSeriesPlayers(seriesId).flatMap { seriesPlayers =>
      if (seriesPlayers.length > 1) {
        seriesRepository.retrieveById(seriesId).map {
          case Some(series) => drawService.drawRobins(seriesPlayers, numberOfRobinGroups, series.setTargetScore, series.numberOfSetsToWin, drawType) match {
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

  def drawBracketRound(roundId: String, seriesId: String, numberOfBracketRounds: Int): Future[Either[DrawError, (String, Bracket)]] = {
    seriesPlayerRepository.retrieveAllSeriesPlayers(seriesId).flatMap { seriesPlayers =>
      if (seriesPlayers.length > 1) {
        seriesRepository.retrieveById(seriesId).map {
          case Some(series) => drawService.drawBracket(seriesPlayers, numberOfBracketRounds, series.numberOfSetsToWin, series.setTargetScore) match {
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

  def advanceSeries(tournamentSeries: TournamentSeries, seriesRounds: List[SeriesRoundWithPlayersAndMatches]): TournamentSeries = {
    if(seriesRounds.length > tournamentSeries.currentRoundNr) tournamentSeries.copy(currentRoundNr = tournamentSeries.currentRoundNr+1) else tournamentSeries
  }



  def calculateSeriesScores(seriesPlayersWithRoundPlayers: List[SeriesPlayerWithRoundPlayers]) = {
    seriesPlayersWithRoundPlayers.map(seriesRoundService.calculatePlayerScore)
  }
}
