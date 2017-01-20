package controllers

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import models.TournamentSeries
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import utils.JsonUtils

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import JsonUtils.ListWrites._
import repositories.mongo.{SeriesRepository, SeriesRoundRepository}
import models.SeriesEvidence._

class SeriesController @Inject()(seriesRepository: SeriesRepository, seriesRoundRepository: SeriesRoundRepository) extends Controller with StrictLogging{

  def createSeries = Action.async{ request =>
    logger.info("received request: "+request.body.toString)
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.seriesReads).map{ series =>
      logger.info(series.toString)
      seriesRepository.create(series).map(x => Created(Json.toJson(series)))
    }.getOrElse(Future(BadRequest))

  }

  def updateSeries(seriesId: String) = Action.async{ request =>
    JsonUtils.parseRequestBody[TournamentSeries](request)(JsonUtils.fullSeriesReads).map{ series =>

      println("parsedSeries "+ series)
      seriesRepository.update(series).map(x => Ok(Json.toJson(series)))
    }.getOrElse(Future(BadRequest))
  }

  def getSeriesOfTournament(tournamentId: String) = Action.async{
    seriesRepository.retrieveAllByField("tournamentId", tournamentId).map(seriesList => Ok(Json.listToJson(seriesList)))
  }

  def deleteSeries(seriesId: String) = Action.async{
    deleteRoundsOfSeries(seriesId).flatMap{ x =>
      seriesRepository.delete(seriesId).map{ result => NoContent}
    }
  }

  def deleteRoundsOfSeries(seriesId: String): Future[List[Unit]] = {
    seriesRoundRepository.retrieveAllByField("seriesId", seriesId).flatMap { seriesRoundList =>
      print(seriesRoundList.mkString("\n"))
      Future.sequence {
        seriesRoundList.map(round => seriesRoundRepository.delete(round.id))
      }
    }
  }
}
