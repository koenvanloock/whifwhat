package controllers

import javax.inject.Inject

import actors.{TournamentActor, TournamentEventStreamActor}
import actors.TournamentActor.{GetActiveTournament, HasActiveTournament, LoadTournament, ReleaseTournament}
import actors.TournamentEventStreamActor.{ActivateTournament, Start}
import akka.actor.ActorSystem
import com.typesafe.scalalogging.StrictLogging
import models.{SeriesWithPlayers, Tournament, TournamentSeries, TournamentWithSeries}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Controller, Request}
import repositories.mongo.{SeriesPlayerRepository, SeriesRepository, TournamentRepository}
import utils.JsonUtils
import utils.JsonUtils.ListWrites._
import models.TournamentEvidence._

import scala.concurrent.duration._
import akka.pattern.ask
import akka.stream.scaladsl.Source
import akka.util.Timeout
import models.halls.{HallOverViewTournament, HallOverviewRound, HallOverviewSeries, HallOverviewWrites}
import models.matches.{PingpongMatch, PingpongGame}
import models.player.{Player, Rank}
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.libs.streams.Streams
import services.HallOverviewService

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class TournamentController @Inject()(system: ActorSystem, tournamentRepository: TournamentRepository, seriesRepository: SeriesRepository, seriesPlayerRepository: SeriesPlayerRepository, hallOverviewService: HallOverviewService) extends Controller with StrictLogging{
  implicit val timeout = Timeout(5 seconds)
  val activeTournamentActor = system.actorOf(TournamentActor.props, "activeTournamentActor")
  val tournamentEventStreamActor = system.actorOf(TournamentEventStreamActor.props, "tournamentEventStreamActor")

  val (out, channel) = Concurrent.broadcast[HallOverViewTournament]
  tournamentEventStreamActor ! Start(channel)

  val tournamentWrites = Json.format[Tournament]

  implicit val halloverviewWrites = HallOverviewWrites.hallOverviewTournamentWrites

  def createTournament() = Action.async{ request =>
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

  def createDefaulSeries(tournament: Tournament, request: Request[AnyContent]) = {
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
        (activeTournamentActor ? LoadTournament(tournament)).mapTo[Either[String, Tournament]].map {
          case Right(activeTournament) =>
            hallOverviewService.getHallOverviewTournament(activeTournament).map{ hallOverViewTournament =>
              tournamentEventStreamActor ! ActivateTournament(hallOverViewTournament)
            }
            Ok(Json.toJson(activeTournament))
          case Left(message) => BadRequest(message)
        }
      case None => Future(BadRequest("The requested tournament doesn't exist"))
    }
  }

    def hasActiveTournament = Action.async{
      (activeTournamentActor ? HasActiveTournament).mapTo[Boolean].map( hasActive =>
      Ok(Json.obj("hasTournament" -> hasActive)))
    }


  def getActiveTournament() = Action.async{
    (activeTournamentActor ? GetActiveTournament).mapTo[Option[Tournament]].flatMap{
      case Some(activeTournament) => hallOverviewService.getHallOverviewTournament(activeTournament).map(tournament => Ok(Json.toJson(tournament)))
      case _ => Future(NotFound(Json.toJson("There is no active tournament")))
    }
  }

  def releaseActiveTournament = Action.async{
    (activeTournamentActor ? ReleaseTournament).mapTo[Option[Tournament]].map{ response =>
      tournamentEventStreamActor ! ReleaseTournament
      Ok
    }
  }

  def activeTournamentStream = Action { implicit req =>
    val source = Source.fromPublisher(Streams.enumeratorToPublisher(out.map(Json.toJson(_)(halloverviewWrites))))

    Ok.chunked(source via EventSource.flow).as("text/event-stream")
  }

}
