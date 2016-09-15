package models.player

import models.Model
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}

case class SeriesPlayer(id: String, player: Player, playerScores: PlayerScores)

  object SeriesPlayerEvidence {

    implicit object isSeriesPlayerModel extends Model[SeriesPlayer] {
      implicit val rankFormat = Json.format[Rank]
      implicit val playerFormat = Json.format[Player]
      implicit val playerScoresFormat = Json.format[PlayerScores]

      override def getId(m: SeriesPlayer): Option[String] = Some(m.id)

      override def setId(id: String)(m: SeriesPlayer): SeriesPlayer = m.copy(id = id)

      override def writes(o: SeriesPlayer): JsObject = Json.format[SeriesPlayer].writes(o)

      override def reads(json: JsValue): JsResult[SeriesPlayer] = Json.format[SeriesPlayer].reads(json)
    }

  }
