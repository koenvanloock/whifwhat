package models

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json._

object TournamentFormat {

  val tournamentWrites = (
    (__ \ "id").write[String] and
      (__ \ "tournamentName").write[String] and
      (__ \ "touranmentDate").write[LocalDate] and
      (__ \ "maximumNumberOfSeriesEntries").write[Int] and
        (__ \ "hasMulipleSeries").write[Boolean] and
        (__ \ "showClub").write[Boolean]
  )(unlift(Tournament.unapply _))

  val tournamentReads = (
    (__ \ "id").read[String] and
      (__ \ "tournamentName").read[String] and
      (__ \ "touranmentDate").read[LocalDate] and
      (__ \ "maximumNumberOfSeriesEntries").read[Int] and
      (__ \ "hasMulipleSeries").read[Boolean] and
      (__ \ "showClub").read[Boolean]
    )(Tournament.apply(_,_,_,_,_,_))

  def writes(tournament: Tournament) = Json.toJson(tournament)(tournamentWrites).asInstanceOf[JsObject]

  def reads(json: JsValue) = Json.fromJson(json)(tournamentReads)
}
