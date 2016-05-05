package services

import models.TournamentSeries

import scala.concurrent.{Future, Awaitable}
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesService extends GenericAtomicCrudService[TournamentSeries]{
  val series=  List(
    TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")
  )

  def getTournamentSeries(seriesId: String): Future[Option[TournamentSeries]] = {
    Future(series.find(series => series.seriesId.contains(seriesId)))
  }
  def getTournamentSeriesOfTournament(tournamentId: String): Future[List[TournamentSeries]] = {
    Future(series.filter(series => series.seriesId.contains(tournamentId)))
  }



}
