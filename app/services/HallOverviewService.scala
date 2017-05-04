package services

import javax.inject.Inject

import models.{SeriesRound, Tournament}
import models.halls.{HallOverViewTournament, HallOverviewRound, HallOverviewSeries}
import models.matches.MatchChecker
import play.api.libs.json.Json
import repositories.mongo.{SeriesRepository, SeriesRoundRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HallOverviewService @Inject()(seriesRepository: SeriesRepository, seriesRoundRepository: SeriesRoundRepository) {


  def convertRoundToHallOverView: (SeriesRound) => HallOverviewRound = seriesRound => {
    val allMatches = seriesRound.matches
    val uncompletedMatches = allMatches.filterNot(MatchChecker.isWon)
    val completePercentage =  (100-uncompletedMatches.length) * 100 / allMatches.length

    HallOverviewRound(seriesRound.id, seriesRound.seriesId, s"ronde ${seriesRound.roundNr}", uncompletedMatches, completePercentage)
  }

  def getHallOverviewTournament(tournament: Tournament): Future[HallOverViewTournament] = {
    retrieveHalloverviewSeriesList(tournament)
      .map(hallSeriesList => HallOverViewTournament(tournament.id, tournament.tournamentName, tournament.tournamentDate, hallSeriesList, Nil))


  }

  private def retrieveHalloverviewSeriesList(tournament: Tournament) = {
    seriesRepository.retrieveAllByField("tournamentId", tournament.id).flatMap { seriesList =>
      Future.sequence {
        seriesList.map { series =>
          seriesRoundRepository.retrieveAllByFields(Json.obj("seriesId" -> series.id, "roundNr" -> series.currentRoundNr)).map { roundList =>
            val rounds = roundList.map(convertRoundToHallOverView)
            HallOverviewSeries(series.id, series.seriesName, series.seriesColor, rounds)
          }
        }
      }
    }
  }
}
