package controllers

import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

import actors.ActiveHallActor.{GetHall, SetActiveHall}
import actors.HallEventStreamActor.{ActivateHall, Start}
import actors.{ActiveHallActor, HallEventStreamActor, HallSocketActor}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.{ByteString, Timeout}
import models.halls.Hall
import models.halls.HallEvidence._
import models.matches.{PingpongMatch, SiteGame}
import models.player.{Player, Rank}
import play.api.http.HttpEntity
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.libs.json.Json
import play.api.libs.streams.{ActorFlow, Streams}
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import services.HallService
import utils.{ControllerUtils, JsonUtils}
import utils.JsonUtils.ListWrites._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class HallController @Inject()(hallService: HallService, implicit val system: ActorSystem, implicit val materializer: Materializer) extends Controller {
  implicit val timeout = Timeout(5 seconds)
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[Hall, Hall]
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  implicit val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
  implicit val gameWrites = Json.format[SiteGame]
  val pingpongMatchReads = Json.reads[PingpongMatch]

  val activeHallActor = system.actorOf(ActiveHallActor.props)
  val hallEventStreamActor = system.actorOf(HallEventStreamActor.props)

  val (out, channel) = Concurrent.broadcast[Hall]
  hallEventStreamActor ! Start(channel)

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
        numberOfRows          <- (json \ "rows").asOpt[Int]
        numberOfTablesPerRow  <- (json \ "tablesPerRow").asOpt[Int]
        name                  <- (json \ "hallName").asOpt[String]
        isGreen               <- (json \ "isGreen").asOpt[Boolean]
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

  def update() = Action.async { request =>
    request.body.asJson.flatMap { json =>
      json.validate[Hall].asOpt.map {
        hall => hallService.update(hall).map{ result =>
          activeHallActor ! SetActiveHall(hall)
          hallEventStreamActor ! ActivateHall(result)
          Ok(Json.toJson(result))
        }
      }
    }.getOrElse(Future(BadRequest("Het request kon niet geparset worden.")))

  }

  def setActiveHall(hallId: String) = Action.async {
    hallService.retrieveById(hallId).flatMap {
      case Some(hall) => (activeHallActor ? SetActiveHall(hall)).mapTo[Option[Hall]].map {
        case Some(hall) =>
          hallEventStreamActor ! ActivateHall(hall)
          NoContent
        case _ => BadRequest("Something went wrong...")
      }
      case _ => Future(BadRequest("De gevraagde zaal werd niet gevonden."))
    }

  }

  def hallStream = Action { implicit req =>
    val source = Source.fromPublisher(Streams.enumeratorToPublisher(out.map(Json.toJson(_))))

    Ok.chunked(source via EventSource.flow).as("text/event-stream")
  }

  def getActiveHall() = Action.async{
    (activeHallActor ? GetHall).mapTo[Option[Hall]].map{
      case Some(hall) => Ok(Json.toJson(hall))
      case _ => BadRequest("no hall active")
    }
  }

  def deleteHall(hallId: String) = Action.async{
    hallService.delete(hallId).map{
      case () =>  NoContent
      case _ => BadRequest("couldn't delete id "+hallId)
    }
  }

  def updateHallWithMatch(hallId: String, row: Int, column: Int) = Action.async{ request =>

    ControllerUtils.parseEntityFromRequestBody(request, pingpongMatchReads).map{ pingpongMatch =>
      hallService.setMatchToTable(hallId, row, column, pingpongMatch).map{
        case Some(hall) =>
          println(LocalDateTime.now + " printing hall")
          hallEventStreamActor ! ActivateHall(hall)
          activeHallActor ! SetActiveHall(hall)
          Ok
        case _ => BadRequest
      }

    }.getOrElse(Future(BadRequest))

  }
}
