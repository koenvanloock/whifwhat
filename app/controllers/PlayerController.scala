package controllers

import javax.inject.Inject

import models.{Rank, Player}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.PlayerService
import scala.concurrent.ExecutionContext.Implicits.global


class PlayerController @Inject()(playerRepository: PlayerService) extends Controller{

  implicit val rankWrites = Json.writes[Rank]
  implicit val playerWrites = Json.writes[Player]

  def getAllPlayers = Action.async{

    playerRepository.getPlayers.map( players => Ok(Json.toJson(players)))
  }


}
