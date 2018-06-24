package models

import play.api.libs.json.JsValue
import play.api.libs.json.Json
import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.Reads._
import play.api.libs.json._

object SeriesFormat {

  val seriesReads = (
    (__ \ "_id").read[String] and
      (__ \ "seriesName").read[String] and
      (__ \ "seriesColor").read[String] and
      (__ \ "numberOfSetsToWin").read[Int] and
      (__ \ "setTargetScore").read[Int] and
      (__ \ "playingWithHandicaps").read[Boolean] and
      (__ \ "extraHandicapForRecs").read[Int] and
      (__ \ "showReferees").read[Boolean] and
      (__ \ "currentRoundNr").read[Int] and
      (__ \ "tournamentId").read[String]
    ) (TournamentSeries.apply(_, _, _, _, _, _, _, _, _, _))

  val seriesWrites = (
    (__ \ "_id").write[String] and
      (__ \ "seriesName").write[String] and
      (__ \ "seriesColor").write[String] and
      (__ \ "numberOfSetsToWin").write[Int] and
      (__ \ "setTargetScore").write[Int] and
      (__ \ "playingWithHandicaps").write[Boolean] and
      (__ \ "extraHandicapForRecs").write[Int] and
      (__ \ "showReferees").write[Boolean] and
      (__ \ "currentRoundNr").write[Int] and
      (__ \ "tournamentId").write[String]
    ) (unlift(TournamentSeries.unapply))

  def writes(tournamentSeries: TournamentSeries) = Json.toJson(tournamentSeries)(seriesWrites).asInstanceOf[JsObject]

  def reads(json: JsValue) = Json.fromJson(json)(seriesReads)
}
