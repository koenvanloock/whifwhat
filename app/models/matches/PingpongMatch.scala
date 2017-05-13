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
                      games: List[PingpongGame])

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
  def isWon(pingpongMatch: PingpongMatch): Boolean = {
    (pingpongMatch.wonSetsA > pingpongMatch.wonSetsB) && pingpongMatch.wonSetsA == pingpongMatch.numberOfSetsToWin ||
      pingpongMatch.wonSetsA < pingpongMatch.wonSetsB && pingpongMatch.wonSetsB == pingpongMatch.numberOfSetsToWin
  }
}
