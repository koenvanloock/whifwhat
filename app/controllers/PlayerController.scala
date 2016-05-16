package controllers

import java.util.UUID
import javax.inject.Inject

import models.{Rank, Player}
import play.api.libs.json.{Reads, JsPath, Json}
import play.api.mvc.{Action, Controller}
import services.PlayerService
import utils.JsonUtils
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax


class PlayerController @Inject()(playerRepository: PlayerService) extends Controller{

  implicit val rankWrites = Json.format[Rank]
  implicit val playerWrites = Json.format[Player]


  val playerReads: Reads[Player] = (
    (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "rank").read[Rank]
    )(Player.apply (UUID.randomUUID().toString,_,_,_))

  def getAllPlayers = Action.async{

    playerRepository.getPlayers.map( players => Ok(Json.toJson(players)))
  }


  def getPlayer(playerId: String) = Action.async{
    playerRepository.getPlayer(playerId).map{
      case Some(player) => Ok(Json.toJson(player))
      case _ => NotFound
    }
  }

  def insertPlayer = Action.async{ request =>
    request.body.asJson.flatMap{ json =>
      json.validate[Player](playerReads).asOpt.map{  player =>
        playerRepository.createPlayer(player).map{
          case Some(createdPlayer) => Created(Json.toJson(createdPlayer))
          case _ => InternalServerError
        }
      }
    }.getOrElse(Future(BadRequest))
  }

  def updatePlayer = Action.async{ request =>
    request.body.asJson.flatMap{ json =>
      json.validate[Player].asOpt.map{player =>
        playerRepository.updatePlayer(player).map{
         case Some(updatedPlayer) => Ok(Json.toJson(updatedPlayer))
         case _ => NotFound
        }
      }}.getOrElse(Future(BadRequest))
  }

  def deletePlayer(playerId: String) = Action.async{
    playerRepository.deletePlayer(playerId).map(_ => NoContent)
  }

}
