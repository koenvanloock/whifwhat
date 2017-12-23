package controllers

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.{Inject, Named}

import actors.TournamentEventActor._
import actors.TournamentEventActor.Hallchanged
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.{ByteString, Timeout}
import models.halls.Hall
import models.halls.HallEvidence._
import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, Rank}
import play.api.libs.json.Json
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import services.{HallService, SeriesRoundService}
import utils.JsonUtils
import utils.JsonUtils.ListWrites._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class HallController @Inject()(@Named("tournament-event-actor") tournamentEventActor: ActorRef, hallService: HallService, seriesRoundService: SeriesRoundService, implicit val system: ActorSystem, implicit val materializer: Materializer) extends InjectedController {
  implicit val timeout = Timeout(5 seconds)
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[Hall, Hall]
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  implicit val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
  implicit val gameWrites = Json.format[PingpongGame]
  val pingpongMatchReads = Json.reads[PingpongMatch]


  def getAllHalls: Action[AnyContent] = Action.async {

    hallService.retrieveAll.map { hallList => Ok(Json.listToJson(hallList)) }

  }


  def handleHallCreateResult: Either[String, Hall] => Result = {
    case Left(error) => BadRequest(error)
    case Right(hall) => Created(Json.toJson(hall))
  }

  def createHallIfNotExists: Action[AnyContent] = Action.async { request =>

    val hallOpt = request.body.asJson.flatMap { json =>
      for {
        numberOfRows <- (json \ "rows").asOpt[Int]
        numberOfTablesPerRow <- (json \ "tablesPerRow").asOpt[Int]
        name <- (json \ "hallName").asOpt[String]
        isGreen <- (json \ "isGreen").asOpt[Boolean]
      } yield (Hall(UUID.randomUUID().toString, name, numberOfRows, numberOfTablesPerRow, Nil), isGreen)
    }

    hallOpt match {
      case Some((hall, isGreen)) => hallService.createIfNotExists(hall, isGreen).map(handleHallCreateResult)
      case None => Future(BadRequest("Kon geen geldige zaal opbouwen"))
    }
  }

  def getById(hallId: String): Action[AnyContent] = Action.async {
    hallService.getHallById(hallId).map {
      case Some(hall) => Ok(Json.toJson(hall))
      case _ => NotFound("Zaal niet gevonden")
    }
  }

  def update() = Action.async(parse.tolerantJson) { request =>
    request.body.validate[Hall].asOpt.map {
        hall =>
          hallService.update(hall).map { result =>
            tournamentEventActor ! Hallchanged(hall)
            Ok(Json.toJson(result))
          }
    }.getOrElse(Future(BadRequest("Het request kon niet geparset worden.")))
  }

  def setActiveHall(hallId: String) = Action.async {
    hallService.retrieveById(hallId).flatMap {
      case Some(hall) => (tournamentEventActor ? Hallchanged(hall)).mapTo[Option[Hall]].map {
        case Some(hall) =>
          NoContent
        case _ => BadRequest("Something went wrong...")
      }
      case _ => Future(BadRequest("De gevraagde zaal werd niet gevonden."))
    }

  }

  def getActiveHall() = Action.async {
    (tournamentEventActor ? GetHall).mapTo[Option[Hall]].map {
      case Some(hall) => Ok(Json.toJson(hall))
      case _ => BadRequest("no hall active")
    }
  }

  def deleteHall(hallId: String) = Action.async {
    hallService.delete(hallId).map {
      case () => NoContent
    }
  }

  def updateHallWithMatch(hallId: String, row: Int, column: Int) = Action.async(parse.tolerantJson) { request =>

    JsonUtils.parseRequestBody(request)(JsonUtils.pingpongMatchReads).map { pingpongMatch =>
      hallService.setMatchToTable(hallId, row, column, pingpongMatch).map {
        case Some(hall) =>
          tournamentEventActor ! MoveMatchInHall(hallId, row, column, pingpongMatch)
          Ok
        case _ => BadRequest
      }
    }.getOrElse(Future(BadRequest))

  }

  def deleteHallMatch(hallId: String, row: Int, column: Int) = Action.async(parse.tolerantJson) { request =>
    JsonUtils.parseRequestBody(request)(pingpongMatchReads).map { pingpongMatch =>
      seriesRoundService.retrieveByFields(Json.obj("id" -> pingpongMatch.roundId)).flatMap {
        case Some(round) =>
          val updatedRound = seriesRoundService.updateMatchInRound(pingpongMatch, round)
          deleteMatchInHall(hallId, row, column, pingpongMatch)

        case _ => Future(BadRequest("round not found, update failed"))
      }
    }.getOrElse(Future(BadRequest("couldn't parse match")))
  }

  private def deleteMatchInHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch): Future[Result] = {
    hallService.deleteMatchInHall(hallId, row, column).map {
      case Some(updatedHall) =>
            seriesRoundService.updateHallMatch(pingpongMatch)
            tournamentEventActor ! HallMatchDelete(hallId, row, column, pingpongMatch)
            Ok
      case _ => BadRequest
    }
  }

  def deleteHallReferee(hallId: String, row: Int, column: Int) = Action.async(parse.tolerantJson) { request =>
    JsonUtils.parseRequestBody(request)(playerFormat).map { referee =>
      hallService.deleteHallRef(hallId, row, column, referee).map {
        case Some(updatedHall) =>
          tournamentEventActor ! HallRefereeDelete(hallId, row, column, referee, completed = false)
          Ok
        case _ => BadRequest
      }
    }.getOrElse(Future(BadRequest))
  }

  def updateHallWithReferee(hallId: String, row: Int, column: Int) = Action.async(parse.tolerantJson) { request =>
    JsonUtils.parseRequestBody(request)(playerFormat).map { referee =>
      hallService.insertRefInHall(hallId, row, column, referee).map {
        case Some(updatedHall) =>
          tournamentEventActor ! HallRefereeInsert(hallId, row, column, referee)
          Ok
        case _ => BadRequest
      }
    }.getOrElse(Future(BadRequest))
  }
}
