package services

import javax.inject.Inject

import models._
import models.player.{SeriesPlayerWithRoundPlayers, SeriesPlayer, Player}

import scala.concurrent.{Future, Awaitable}
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesService @Inject()(drawService: DrawService, seriesRoundService: SeriesRoundService) extends GenericAtomicCrudService[TournamentSeries]{
  val series=  List(
    TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")
  )

  def getTournamentSeries(seriesId: String): Future[Option[TournamentSeries]] = {
    Future(series.find(series => series.seriesId.contains(seriesId)))
  }
  def getTournamentSeriesOfTournament(tournamentId: String): Future[List[TournamentSeries]] = {
    Future(series.filter(series => series.seriesId.contains(tournamentId)))
  }

  def drawSeries(seriesRound: SeriesRound, seriesPlayers: List[SeriesPlayer], numberOfSetsToWin: Int, setTargetScore: Int, drawType: DrawType = DrawTypes.EnteredOrder): Option[SeriesRoundWithPlayersAndMatches] = seriesRound match {
    case robin:SiteRobinRound => drawService.drawRobins(seriesPlayers,robin.numberOfRobins, numberOfSetsToWin, setTargetScore, drawType)
    case bracket:SiteBracketRound => drawService.drawBracket(seriesPlayers, bracket.numberOfBracketRounds,numberOfSetsToWin, setTargetScore)
  }

  def advanceSeries(tournamentSeries: TournamentSeries, seriesRounds: List[SeriesRoundWithPlayersAndMatches]): TournamentSeries = {
    if(seriesRounds.length > tournamentSeries.currentRoundNr) tournamentSeries.copy(currentRoundNr = tournamentSeries.currentRoundNr+1) else tournamentSeries
  }



  def calculateSeriesScores(seriesPlayersWithRoundPlayers: List[SeriesPlayerWithRoundPlayers]) = {
    seriesPlayersWithRoundPlayers.map(seriesRoundService.calculatePlayerScore)
  }
}
