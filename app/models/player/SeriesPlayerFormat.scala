package models.player

import play.api.libs.json.Json
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.Reads._
import play.api.libs.json._

object SeriesPlayerFormat {
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  implicit val playerScoresFormat = Json.format[PlayerScores]

  val readSeriesPlayer = (
    (__ \ "_id").read[String] and
      (__ \ "seriesId").read[String] and
      (__ \ "player").read[Player] and
      (__ \ "playerScores").read[PlayerScores]
  ) (SeriesPlayer.apply(_,_,_,_))

  val writeSeriesPlayer = (
    (__ \ "_id").write[String] and
      (__ \ "seriesId").write[String] and
      (__ \ "player").write[Player] and
      (__ \ "playerScores").write[PlayerScores]
    ) (unlift(SeriesPlayer.unapply))

  def reads(json: JsValue) = Json.fromJson(json)(readSeriesPlayer)
  def writes(seriesPlayer: SeriesPlayer) = Json.toJson(seriesPlayer)(writeSeriesPlayer).asInstanceOf[JsObject]
}
