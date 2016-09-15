package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import models.{SeriesWithPlayers, Tournament, TournamentWithSeries}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository, TournamentRepository}
import utils.JsonUtils
import utils.JsonUtils.ListWrites._
import models.TournamentEvidence._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TournamentController @Inject()(tournamentRepository: TournamentRepository, seriesRepository: SeriesRepository, seriesPlayerRepository: SeriesPlayerRepository) extends Controller with StrictLogging{

 val tournamentWrites = Json.format[Tournament]

  def createTournament() = Action.async{ request =>
    logger.info("received request: "+request.body.toString)
    JsonUtils.parseRequestBody[Tournament](request)(JsonUtils.tournamentReads).map{ tournament =>
         logger.info(tournament.toString)
        tournamentRepository.create(tournament).map(x => Created(Json.toJson(tournament)))
    }.getOrElse(Future(BadRequest))

  }

  def getTournamentById(tournamentId: String) = Action.async{
    tournamentRepository.retrieveById(tournamentId).flatMap{
      case Some(tournament) => seriesRepository.retrieveAll().flatMap { seriesList =>
        Future.sequence{seriesList.map{ series =>
          seriesPlayerRepository.retrieveAllSeriesPlayers(series.id).map{ seriesPlayers =>
              SeriesWithPlayers(series.id, series.seriesName, series.seriesColor, series.numberOfSetsToWin, series.setTargetScore,series.playingWithHandicaps, series.extraHandicapForRecs, series.showReferees, series.currentRoundNr, seriesPlayers,series.tournamentId)
          }
        }
      }.map{ seriesWithPlayerList => Ok(Json.toJson(TournamentWithSeries(tournament, seriesWithPlayerList))(JsonUtils.tournamentWithSeriesWrites))}
      }
      case _ => Future(NotFound)
    }
  }

  def getAllTournaments = Action.async{
    tournamentRepository.retrieveAll().map(tounaments => Ok(Json.listToJson(tounaments)(tournamentWrites)))
  }
}
