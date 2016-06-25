package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import models.TournamentSeries
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import repositories.SeriesRepository
import utils.JsonUtils
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import JsonUtils.ListWrites._

class SeriesController @Inject()(seriesRepository: SeriesRepository) extends Controller with StrictLogging{
  val seriesFormat = Json.format[TournamentSeries]

  def createSeries = Action.async{ request =>
    logger.info("received request: "+request.body.toString)
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.seriesReads).map{ series =>
      logger.info(series.toString)
      seriesRepository.create(series).map(x => Created(Json.toJson(series)(seriesFormat)))
    }.getOrElse(Future(BadRequest))

  }

  def updateSeries(seriesId: String) = Action.async{ request =>
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.seriesReads).map{ series =>
      seriesRepository.update(series).map(x => Ok(Json.toJson(series)(seriesFormat)))
    }.getOrElse(Future(BadRequest))
  }

  def getSeriesOfTournament(tournamentId: String) = Action.async{
    seriesRepository.retrieveAllByField("TOURNAMENT_ID", tournamentId).map(seriesList => Ok(Json.listToJson(seriesList)(seriesFormat)))
  }
}
