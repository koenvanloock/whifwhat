package controllers

import javax.inject.{Inject, Named}

import actors.TournamentEventActor._
import akka.actor.{ActorRef, ActorSystem}
import com.typesafe.scalalogging.StrictLogging
import models.{SeriesWithPlayers, Tournament, TournamentWithSeries}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository, TournamentRepository}
import utils.JsonUtils
import utils.JsonUtils.ListWrites._
import models.TournamentEvidence._
import akka.util.Timeout

import scala.concurrent.duration._
import akka.pattern.ask
import models.halls.HallOverViewTournament
import jsonconverters.HallTournamentOverviewWrites
import services.HallOverviewService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TournamentController @Inject()(@Named("tournament-event-actor") tournamentEventActor: ActorRef, system: ActorSystem, tournamentRepository: TournamentRepository, seriesRepository: SeriesRepository, seriesPlayerRepository: SeriesPlayerRepository, hallOverviewService: HallOverviewService) extends InjectedController with StrictLogging{
  implicit val timeout = Timeout(5 seconds)
  val tournamentWrites = Json.format[Tournament]



  implicit val halloverviewWrites = HallTournamentOverviewWrites.hallOverviewTournamentWrites

  def createTournament() = Action.async(parse.tolerantJson){ request =>
    JsonUtils.parseRequestBody[Tournament](request)(JsonUtils.tournamentReads).map{ tournament =>
        tournamentRepository.create(tournament).flatMap { createdTournament =>
          if (!createdTournament.hasMultipleSeries) {
            createDefaulSeries(createdTournament, request)
          } else {
            Future(Created(Json.toJson(createdTournament)))
          }
        }
    }.getOrElse(Future(BadRequest("Tornooi kon niet aangemaakt worden.")))

  }

  def createDefaulSeries(tournament: Tournament, request: Request[JsValue]) = {
    JsonUtils.parseRequestBody(request)(JsonUtils.singleSeriesReads).map { tournamentSeries =>
      println("testCreate")
      seriesRepository.create(tournamentSeries.copy(tournamentId = tournament.id, seriesName = tournament.tournamentName)).map { _ =>
         Created(Json.toJson(tournament))
      }
    }.getOrElse(Future(BadRequest("De reeks kon niet gecreërd worden")))
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


  def activateTournament(tournamentId: String) = Action.async {
    tournamentRepository.retrieveById(tournamentId).flatMap {
      case Some(tournament) =>
            hallOverviewService.getHallOverviewTournament(tournament).flatMap { hallOverViewTournament =>
              (tournamentEventActor ? ActiveTournamentChanged(hallOverViewTournament)).mapTo[Option[HallOverViewTournament]].map {
                case Some(activeTournament) => Ok(Json.toJson(tournament))
                case None => BadRequest("The tournament couldn't be activated")
              }
            }
      case None => Future(BadRequest("The requested tournament doesn't exist"))
    }
  }

    def hasActiveTournament = Action.async{
      (tournamentEventActor ? HasActiveTournament).mapTo[Boolean].map( hasActive =>
      Ok(Json.obj("hasTournament" -> hasActive)))
    }


  def getActiveTournament() = Action.async{
    (tournamentEventActor ? GetActiveTournament).mapTo[Option[HallOverViewTournament]].map{
      case Some(activeTournament) =>  Ok(Json.toJson(activeTournament))
      case _ => NotFound(Json.toJson("There is no active tournament"))
    }
  }

  def releaseActiveTournament = Action.async{
    (tournamentEventActor ? ActiveTournamentRemoved).mapTo[String].map{ response => Ok
    }
  }
}
