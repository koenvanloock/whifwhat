package services

import javax.inject.Inject

import models.{SeriesRound, Tournament}
import models.halls.{HallOverViewTournament, HallOverviewRound, HallOverviewSeries}
import models.matches.{MatchChecker, PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, RefereeInfo, ViewablePlayer}
import play.api.libs.json.Json
import repositories.mongo.{SeriesRepository, SeriesRoundRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HallOverviewService @Inject()(seriesRepository: SeriesRepository, seriesRoundRepository: SeriesRoundRepository, seriesPlayerService: SeriesPlayerService) {


  def convertRoundToHallOverView: (SeriesRound) => (HallOverviewRound, List[PingpongMatch]) = seriesRound => {
    val allMatches = seriesRound.matches
    val uncompletedMatches = allMatches.filterNot(MatchChecker.isWon)
    val completePercentage =  (allMatches.length - uncompletedMatches.length) * 100 / (allMatches.length + 1)

    (HallOverviewRound(seriesRound.id, seriesRound.seriesId, s"ronde ${seriesRound.roundNr}", completePercentage), uncompletedMatches)
  }

  def getHallOverviewTournament(tournament: Tournament): Future[HallOverViewTournament] = {
    retrieveHalloverviewSeriesList(tournament)
      .flatMap{ case(hallSeriesListWithMatchesToPlay) =>
        val hallSeriesList = hallSeriesListWithMatchesToPlay.map(_._1)
        val matchList = hallSeriesListWithMatchesToPlay.flatMap(_._2).map(ppMatch => ViewablePingpongMatch(ppMatch, MatchChecker.isWon(ppMatch), isOccupied = false))
        seriesPlayerService.retrievePlayersOf(hallSeriesList.map(_.seriesId)).map{seriesPlayerList =>
          val players = seriesPlayerList.map(_.player).distinct.map{ player =>
            val numberOfPlayerMatches = matchList.count(pingpongMatch => getMatchPlayers(pingpongMatch.pingpongMatch).contains(player))
            ViewablePlayer(player, RefereeInfo(0,numberOfPlayerMatches), occupied = false)}
        HallOverViewTournament(tournament.id, tournament.tournamentName, tournament.tournamentDate, hallSeriesList, players, matchList)}
      }
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

  private def getMatchPlayers(pingpongMatch: PingpongMatch): List[Player] =
    pingpongMatch.playerA.toList ++ pingpongMatch.playerB.toList
}
