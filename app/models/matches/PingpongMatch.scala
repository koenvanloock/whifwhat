package models.matches

import models.player.{Player, Rank}
import models.Model
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}
import utils.JsonUtils


case class PingpongMatch(
                      id: String,
                      playerA: Option[Player],
                      playerB: Option[Player],
                      roundId: String,
                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int,
                      wonSetsA: Int,
                      wonSetsB: Int,
                      games: List[PingpongGame],
                      errorMessage:  Option[String] = None) extends repositories.numongo.Model[String]

object MatchEvidence{

  implicit object matchIsModel extends Model[PingpongMatch]{
    override def getId(m: PingpongMatch): Option[String] = Some(m.id)

    override def setId(id: String)(m: PingpongMatch): PingpongMatch = m.copy(id = id)

    implicit val rankFormat = Json.format[Rank]
    implicit val playerFormat = Json.format[Player]
    val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
    implicit val gameWrites = Json.format[PingpongGame]
    override def writes(o: PingpongMatch): JsObject = Json.format[PingpongMatch].writes(o)

    override def reads(json: JsValue): JsResult[PingpongMatch] = Json.format[PingpongMatch].reads(json)
  }
}

object MatchChecker{
  def calculateSets(pingpongMatch: PingpongMatch): PingpongMatch = {
    val (wonSetsA, wonSetsB) = pingpongMatch.games.foldLeft[(Int, Int)]((0,0)){ (acc, game) => {
      if( (game.pointA == pingpongMatch.targetScore && game.pointB <= pingpongMatch.targetScore - 2 ) ||
        (game.pointA - 2 == game.pointB && game.pointA >= pingpongMatch.targetScore)){
       (acc._1 + 1, acc._2)
      } else if( (game.pointB == pingpongMatch.targetScore && game.pointA <= pingpongMatch.targetScore - 2 ) ||
        (game.pointB - 2 == game.pointA && game.pointB >= pingpongMatch.targetScore)){
        (acc._1 , acc._2 + 1)
      } else {
        acc
      }
    }}

    val gameError = pingpongMatch.games.foldLeft[Option[String]](None)( (acc, game) =>  if(acc.isEmpty && allowedGameScore(game, pingpongMatch.targetScore)) None else if(acc.isEmpty) Some(s"Error in set ${game.gameNr}") else acc)
    val finalError = gameError match {
      case Some(error)  => Some(error)
      case _            => if(
        !pingpongMatch.games.forall(gameEmpty) &&
          wonSetsA != pingpongMatch.numberOfSetsToWin &&
          wonSetsB != pingpongMatch.numberOfSetsToWin
      ) Some("Deze wedstrijd is onvolledig of foutief ingevuld!") else None
    }
    pingpongMatch.copy(wonSetsA = wonSetsA, wonSetsB = wonSetsB, errorMessage = finalError)
  }

  def isWon(pingpongMatch: PingpongMatch): Boolean = {
    val checkecMatch = calculateSets(pingpongMatch)
    (checkecMatch.wonSetsA > checkecMatch.wonSetsB) && checkecMatch.wonSetsA == checkecMatch.numberOfSetsToWin ||
      checkecMatch.wonSetsA < checkecMatch.wonSetsB && checkecMatch.wonSetsB == checkecMatch.numberOfSetsToWin
  }

  def gameEmpty(pingpongGame: PingpongGame) = pingpongGame.pointA == 0 && pingpongGame.pointB == 0

  def allowedGameScore(pingpongGame: PingpongGame, targetScore: Int): Boolean = {
    if(pingpongGame.pointA != 0 || pingpongGame.pointB != 0){
      if(pingpongGame.pointA != targetScore && pingpongGame.pointB != targetScore){
        Math.abs(pingpongGame.pointA - pingpongGame.pointB) == 2
      } else {
        Math.abs(pingpongGame.pointA - pingpongGame.pointB) > 1
      }
    } else {
      true
    }
  }
}
