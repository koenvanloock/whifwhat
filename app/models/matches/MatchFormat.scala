package models.matches

import models.player.{Player, Rank}
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json._
import utils.JsonUtils

import scala.language.postfixOps

object MatchFormat {
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)
  implicit val gameFormat = Json.format[PingpongGame]

  implicit val matchReads: Reads[PingpongMatch] = (
    (__ \ "_id").read[String] and
      (__ \ "playerA").readNullable[Player] and
      (__ \ "playerB").readNullable[Player] and
      (__ \ "roundId").read[String] and
      (__ \ "handicap").read[Int] and
      (__ \ "isHandicapForB").read[Boolean] and
      (__ \ "targetScore").read[Int] and
      (__ \ "numberOfSetsToWin").read[Int] and
      (__ \ "wonSetsA").read[Int] and
      (__ \ "wonSetsA").read[Int] and
      (__ \ "games").lazyRead[List[PingpongGame]](Reads.list(Json.reads[PingpongGame])) and
      ( __ \ "errorMessage").readNullable[String]
    ) (PingpongMatch.apply(_,_,_,_,_,_,_,_,_,_,_,_))

  implicit val matchWrites: Writes[PingpongMatch] = (
    (__ \ "_id").write[String] and
      (__ \ "playerA").writeNullable[Player] and
      (__ \ "playerB").writeNullable[Player] and
      (__ \ "roundId").write[String] and
      (__ \ "handicap").write[Int] and
      (__ \ "isHandicapForB").write[Boolean] and
      (__ \ "targetScore").write[Int] and
      (__ \ "numberOfSetsToWin").write[Int] and
      (__ \ "wonSetsA").write[Int] and
      (__ \ "wonSetsA").write[Int] and
      (__ \ "games").lazyWrite[List[PingpongGame]](Writes.list(Json.writes[PingpongGame])) and
      ( __ \ "errorMessage").writeNullable[String]
    ) (unlift(PingpongMatch.unapply))
  def writes(o: PingpongMatch): JsObject = matchWrites.asInstanceOf[JsObject]

  def reads(json: JsValue): JsResult[PingpongMatch] = Json.fromJson(json)(matchReads)
}