package controllers

import java.util.UUID
import javax.inject.Inject

import models.TournamentSeries
import models.player._
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, InjectedController}
import services.{PlayerService, SeriesPlayerService}
import utils.JsonUtils.ListWrites._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._ // Combinator syntax


class PlayerController @Inject()(playerService: PlayerService, seriesPlayerService: SeriesPlayerService) extends InjectedController{

  implicit val rankWrites = Json.format[Rank]
  implicit val playerWrites = Json.format[Player]
  implicit val playerScoreWrites = Json.writes[PlayerScores]
  implicit val seriesPlayerWrites = Json.writes[SeriesPlayer]


  val playerReads: Reads[Player] = (
    (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "rank").read[Rank]
    )(Player.apply (UUID.randomUUID().toString,_,_,_))

  val fullPlayerReads: Reads[Player] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "rank").read[Rank]
    )(Player.apply (_,_,_,_))

  def getAllPlayers: Action[AnyContent] = Action.async{
    playerService.getPlayers.map(players => Ok(Json.toJson(players)))
  }

  def getPlayer(playerId: String): Action[AnyContent] = Action.async{
    playerService.getPlayer(playerId).map{
      case Some(player) => Ok(Json.toJson(player))
      case _ => NotFound
    }
  }

  def insertPlayer: Action[JsValue] = Action.async(parse.tolerantJson){ request =>
    request.body.validate[Player](playerReads).asOpt.map{  player =>
        playerService.createPlayer(player).map{
          createdPlayer => Created(Json.toJson(createdPlayer))
        }
    }.getOrElse(Future(BadRequest))
  }

  def updatePlayer: Action[JsValue] = Action.async(parse.tolerantJson){ request =>
    request.body.validate[Player].asOpt.map{player =>
        playerService.updatePlayer(player).map{
         updatedPlayer => Ok(Json.toJson(updatedPlayer))
        }
      }.getOrElse(Future(BadRequest))
  }

  def deletePlayer(playerId: String): Action[AnyContent] = Action.async{
    playerService.deletePlayer(playerId).map(_ => NoContent)
  }

  def getRanks = Action {
    Ok(Json.toJson(Ranks.RankList.map(Json.toJson(_))))
  }

  def getSeriesPlayers(seriesId: String): Action[AnyContent] = Action.async(playerService.getSeriesPlayers(seriesId).map(players => Ok(Json.listToJson(players))))

  def enterSubscriptions(): Action[JsValue] = Action.async(parse.tolerantJson){ request =>
      deletePreviousTournamentSeriesSubscriptions(request.body).flatMap { numberOfDeletes =>

        parseSeriesPlayers(request.body).map{ seriesPlayers =>
          seriesPlayerService.subscribePlayers(seriesPlayers).map(_ => Created(Json.listToJson(seriesPlayers)(seriesPlayerWrites)))
        }
    }.getOrElse(Future(BadRequest("Geef een geldige speler op.")))
  }

  def parseSeriesPlayers(json: JsValue): Option[List[SeriesPlayer]] = {
    (json \ "player").asOpt[Player](fullPlayerReads).flatMap{ player =>
      (json \ "subscriptions").asOpt[JsArray].map { array =>
        array.value.map{  seriesSubscriptionIdJson =>
          seriesSubscriptionIdJson.asOpt[String].map{ seriesSubscriptionId =>
            SeriesPlayer(UUID.randomUUID().toString, seriesSubscriptionId, player, PlayerScores())
          }
        }.toList.flatten
      }
    }
  }

  def deletePreviousTournamentSeriesSubscriptions(json: JsValue): Option[Future[Seq[Int]]] = {
    (json \ "player").asOpt[Player](fullPlayerReads).flatMap { player =>
      (json \ "seriesList").asOpt[JsArray].map { array =>
        Future.sequence{array.value.flatMap { jsVal => jsVal.validate[String].asOpt }.map { seriesId =>
          playerService.deleteSubscriptions(seriesId, player) }}
      }
    }
  }

  def getSubscriptionsOfTournamentPlayer(playerId: String, tournamentId: String): Action[AnyContent] = Action.async{
    playerService.getSeriesOfPlayer(playerId, tournamentId).map{
      series => Ok(Json.listToJson(series)(Json.writes[TournamentSeries]))
    }
  }

  def searchPlayers(searchString: Option[String]): Action[AnyContent] = Action.async{
    searchString match {
      case Some(search) => playerService.findByString(search).map(players => Ok(Json.toJson(players)))
      case None =>     playerService.getPlayers.map(players => Ok(Json.toJson(players)))
    }

  }
}
