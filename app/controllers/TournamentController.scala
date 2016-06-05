package controllers

import javax.inject.Inject


import com.typesafe.scalalogging.StrictLogging
import models.{TournamentResultModel, Tournament}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import repositories.TournamentRepository
import services.TournamentService
import utils.JsonUtils
import utils.JsonUtils.ListWrites._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TournamentController @Inject()(tournamentRepository: TournamentRepository) extends Controller with StrictLogging{

 val tournamentWrites = Json.format[Tournament]

  def createTournament() = Action.async{ request =>
    logger.info("received request: "+request.body.toString)
    JsonUtils.parseRequestBody[Tournament](request)(JsonUtils.tournamentReads).map{ tournament =>
        tournamentRepository.create(tournament).map(x => Created(Json.toJson(tournament)(tournamentWrites)))
    }.getOrElse(Future(BadRequest))

  }

  def getTournamentById(tournamentId: String) = Action.async{
    tournamentRepository.retrieveById(tournamentId).map{
      case Some(tournament) => Ok(Json.toJson(tournament)(tournamentWrites))
      case _ => NotFound
    }
  }

  def getAllTournaments = Action.async{
    tournamentRepository.retrieveAll().map(tounaments => Ok(Json.listToJson(tounaments)(tournamentWrites)))
  }
}
