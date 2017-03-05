package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import models.SeriesEvidence._
import models.player.SeriesPlayer
import models.{ScoreTypes, SeriesRound, TournamentSeries}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Result}
import services.{SeriesRoundService, SeriesService}
import utils.JsonUtils.ListWrites._
import utils.{JsonUtils, RoundRankingCalculator}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesController @Inject()(seriesService: SeriesService, seriesRoundService: SeriesRoundService) extends Controller with StrictLogging {

  def createSeries = Action.async { request =>
    logger.info("received request: " + request.body.toString)
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.seriesReads).map { series =>
      logger.info(series.toString)
      seriesService.create(series).map(x => Created(Json.toJson(series)))
    }.getOrElse(Future(BadRequest))

  }

  def updateSeries(seriesId: String) = Action.async { request =>
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.fullSeriesReads).map { series =>
      seriesService.update(series).map(x => Ok(Json.toJson(series)))
    }.getOrElse(Future(BadRequest))
  }

  def getSeriesOfTournament(tournamentId: String) = Action.async {
    seriesService.retrieveAllByField("tournamentId", tournamentId).map(seriesList => Ok(Json.listToJson(seriesList)))
  }

  def deleteSeries(seriesId: String) = Action.async {
    deleteRoundsOfSeries(seriesId).flatMap { x =>
      seriesService.delete(seriesId).map { result => NoContent }
    }
  }

  def deleteRoundsOfSeries(seriesId: String): Future[List[Unit]] = {
    seriesRoundService.retrieveAllByField("seriesId", seriesId).flatMap { seriesRoundList =>
      Future.sequence {
        seriesRoundList.map(round => seriesRoundService.delete(round.id))
      }
    }
  }

  def getSeries(seriesId: String) = Action.async {
    seriesService.retrieveById(seriesId).map {
      case Some(series) => Ok(Json.toJson(series))
      case _ => BadRequest("series not found")
    }
  }
}
