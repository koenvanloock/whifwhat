package services

import javax.inject.Inject

import models._
import models.player.SeriesPlayerWithRoundPlayers
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesService @Inject()(seriesRoundService: SeriesRoundService, seriesPlayerRepository: SeriesPlayerRepository, seriesRepository: SeriesRepository, drawService: DrawService){
  val series=  List(
    TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")
  )

  def getTournamentSeries(seriesId: String): Future[Option[TournamentSeries]] = {
    Future(series.find(series => series.id.contains(seriesId)))
  }
  def getTournamentSeriesOfTournament(tournamentId: String): Future[List[TournamentSeries]] = {
    Future(series.filter(series => series.id.contains(tournamentId)))
  }


  def drawTournamentFirstRounds(tournamentId: String): Future[List[Either[DrawError, (String, SeriesRoundWithPlayersAndMatches)]]] = {
    getStartingRoundsOfTournament(tournamentId).flatMap { roundList =>
      Future.sequence {
        roundList.map(round => drawRound(round))
      }
    }
  }


  def getStartingRoundsOfTournament(tournamentId: String): Future[List[SeriesRound]] = seriesRepository.retrieveAllByField("TOURNAMENT_ID", tournamentId).flatMap { seriesList =>
    Future.sequence {
      seriesList.map {
        series => seriesRoundService.getRoundsOfSeries(series.id).map(_.sortBy(_.roundNr).headOption)
      }
    }
  }.map(_.flatten)

  def drawRound(round: SeriesRound): Future[Either[DrawError, (String, SeriesRoundWithPlayersAndMatches)]] = round match {
    case robinRound: SiteRobinRound => drawRobinRound(robinRound.id, robinRound.seriesId, robinRound.numberOfRobinGroups)
    case bracketRound: SiteBracketRound => drawBracketRound(bracketRound.id, bracketRound.seriesId, bracketRound.numberOfBracketRounds)
  }

  def drawRobinRound(roundId: String, seriesId: String, numberOfRobinGroups: Int, drawType: DrawType = DrawTypes.RankedRandomOrder): Future[Either[DrawError, (String, RobinRound)]] = {
    seriesPlayerRepository.retrieveAllSeriesPlayers(seriesId).flatMap { seriesPlayers =>
      if (seriesPlayers.length > 1) {
        seriesRepository.retrieveById(seriesId).map {
          case Some(foundSeries) => drawService.drawRobins(seriesPlayers, numberOfRobinGroups, foundSeries.setTargetScore, foundSeries.numberOfSetsToWin, drawType) match {
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
          case Some(foundSeries) => drawService.drawBracket(seriesPlayers, numberOfBracketRounds, foundSeries.numberOfSetsToWin, foundSeries.setTargetScore) match {
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
