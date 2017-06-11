package services

import javax.inject.Inject

import models.{SeriesRound, Tournament}
import models.halls.{HallOverViewTournament, HallOverviewRound, HallOverviewSeries}
import models.matches.{MatchChecker, PingpongMatch}
import play.api.libs.json.Json
import repositories.mongo.{SeriesRepository, SeriesRoundRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HallOverviewService @Inject()(seriesRepository: SeriesRepository, seriesRoundRepository: SeriesRoundRepository) {


  def convertRoundToHallOverView: (SeriesRound) => (HallOverviewRound, List[PingpongMatch]) = seriesRound => {
    val allMatches = seriesRound.matches
    val uncompletedMatches = allMatches.filterNot(MatchChecker.isWon)
    val completePercentage =  uncompletedMatches.length * 100 / allMatches.length

    (HallOverviewRound(seriesRound.id, seriesRound.seriesId, s"ronde ${seriesRound.roundNr}", completePercentage), uncompletedMatches)
  }

  def getHallOverviewTournament(tournament: Tournament): Future[HallOverViewTournament] = {
    retrieveHalloverviewSeriesList(tournament)
      .map{ case(hallSeriesListWithMatchesToPlay) =>
        val hallSeriesList = hallSeriesListWithMatchesToPlay.map(_._1)
        val matchList = hallSeriesListWithMatchesToPlay.flatMap(_._2)
        HallOverViewTournament(tournament.id, tournament.tournamentName, tournament.tournamentDate, hallSeriesList, Nil, matchList)}
  }

  private def retrieveHalloverviewSeriesList(tournament: Tournament): Future[List[(HallOverviewSeries, List[PingpongMatch])]] = {
    seriesRepository.retrieveAllByField("tournamentId", tournament.id).flatMap { seriesList =>
      Future.sequence {
        seriesList.map { series =>
          seriesRoundRepository.retrieveAllByFields(Json.obj("seriesId" -> series.id, "roundNr" -> series.currentRoundNr)).map { roundList =>
            val convertedRounds = roundList.map(convertRoundToHallOverView)
            (HallOverviewSeries(series.id, series.seriesName, series.seriesColor, convertedRounds.map(_._1)), convertedRounds.flatMap(_._2))
          }
        }
      }
    }
  }
}
