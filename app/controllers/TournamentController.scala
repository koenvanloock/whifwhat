package controllers

import javax.inject.Inject

import actors.TournamentActor
import actors.TournamentActor.{GetActiveTournament, LoadTournament}
import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import models.{SeriesWithPlayers, Tournament, TournamentWithSeries}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository, TournamentRepository}
import utils.JsonUtils
import utils.JsonUtils.ListWrites._
import models.TournamentEvidence._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TournamentController @Inject()(system: ActorSystem, tournamentRepository: TournamentRepository, seriesRepository: SeriesRepository, seriesPlayerRepository: SeriesPlayerRepository) extends Controller with StrictLogging{
  implicit val timeout = Timeout(5 seconds)
  val activeTournamentActor = system.actorOf(TournamentActor.props, "activeTournament-actor")
 val tournamentWrites = Json.format[Tournament]

  def createTournament() = Action.async{ request =>
    JsonUtils.parseRequestBody[Tournament](request)(JsonUtils.tournamentReads).map{ tournament =>
         logger.info(tournament.toString)
        tournamentRepository.create(tournament).map(x => Created(Json.toJson(tournament)))
    }.getOrElse(Future(BadRequest))

  }

  def getTournamentById(tournamentId: String) = Action.async{
    tournamentRepository.retrieveById(tournamentId).flatMap{
      case Some(tournament) => seriesRepository.retrieveAllByField("tournamentId", tournamentId).flatMap { seriesList =>
        Future.sequence{seriesList.map{ series =>
          seriesPlayerRepository.retrieveAllSeriesPlayers(series.id).map{ seriesPlayers =>
              SeriesWithPlayers(series.id, series.seriesName, series.seriesColor, series.numberOfSetsToWin, series.setTargetScore,series.playingWithHandicaps, series.extraHandicapForRecs, series.showReferees, series.currentRoundNr, seriesPlayers,series.tournamentId)
          }
        }
      }.map{ seriesWithPlayerList => Ok(Json.toJson(TournamentWithSeries(tournament, seriesWithPlayerList))(JsonUtils.tournamentWithSeriesWritesOnlyPlayers))}
      }
      case _ => Future(NotFound)
    }
  }

  def getAllTournaments = Action.async{
    tournamentRepository.retrieveAll().map(tounaments => Ok(Json.listToJson(tounaments)(tournamentWrites)))
  }


  def activateTournament(tournamentId: String) = Action.async{


    tournamentRepository.retrieveById(tournamentId).flatMap{
      case Some(tournament) =>
        (activeTournamentActor ? LoadTournament(tournament)).mapTo[Either[String, Tournament]].map{
          case Right(activeTournament) => Ok(Json.toJson(activeTournament))
          case Left(message) => BadRequest(message)
        }
      case None => Future(BadRequest("The requested tournament doesn't exist"))
    }


  }

  def getActiveTournament() = Action.async{
    (activeTournamentActor ? GetActiveTournament).mapTo[Option[Tournament]].map{
      case Some(activeTournament) => Ok(Json.toJson(activeTournament))
      case _ => NotFound(Json.toJson("There is no active tournament"))
    }

  }
}
