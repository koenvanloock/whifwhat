package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import models.SeriesEvidence._
import models.TournamentSeries
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import services.{SeriesRoundService, SeriesService}
import utils.JsonUtils.ListWrites._
import utils.JsonUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesController @Inject()(seriesService: SeriesService, seriesRoundService: SeriesRoundService) extends InjectedController with StrictLogging {

  def createSeries: Action[JsValue] = Action.async(parse.tolerantJson) { request =>
    logger.info("received request: " + request.body.toString)
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.seriesReads).map { series =>
      logger.info(series.toString)
      seriesService.create(series).map(_ => Created(Json.toJson(series)))
    }.getOrElse(Future(BadRequest))

  }

  def updateSeries(seriesId: String): Action[JsValue] = Action.async(parse.tolerantJson) { request =>
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.fullSeriesReads).map { series =>
      seriesService.update(series).map(_ => Ok(Json.toJson(series)))
    }.getOrElse(Future(BadRequest))
  }

  def getSeriesOfTournament(tournamentId: String): Action[AnyContent] = Action.async {
    seriesService.retrieveAllByField("tournamentId", tournamentId).map(seriesList => Ok(Json.listToJson(seriesList)))
  }

  def deleteSeries(seriesId: String): Action[AnyContent] = Action.async {
    deleteRoundsOfSeries(seriesId).flatMap { _ =>
      seriesService.delete(seriesId).map { _ => NoContent }
    }
  }

  def deleteRoundsOfSeries(seriesId: String): Future[List[Unit]] = {
    seriesRoundService.retrieveAllByField("seriesId", seriesId).flatMap { seriesRoundList =>
      Future.sequence {
        seriesRoundList.map(round => seriesRoundService.delete(round.id))
      }
    }
  }

  def getSeries(seriesId: String): Action[AnyContent] = Action.async {
    seriesService.retrieveById(seriesId).map {
      case Some(series) => Ok(Json.toJson(series))
      case _ => BadRequest("series not found")
    }
  }
}
