package models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import play.api.libs.json._
import utils.JsonUtils._
import slick.jdbc.GetResult


case class Tournament(id: String, tournamentName: String, tournamentDate: LocalDate, maximumNumberOfSeriesEntries: Int, hasMultipleSeries: Boolean, showClub: Boolean)

  object TournamentEvidence {

    implicit object isTournamentModel extends Model[Tournament] {
      override def getId(tournament: Tournament): Option[String] = Some(tournament.id)

      override def setId(newId: String)(tournament: Tournament): Tournament = tournament.copy(id = tournament.id)

      override def writes(o: Tournament): JsObject = Json.format[Tournament].writes(o)

      override def reads(json: JsValue): JsResult[Tournament] = Json.format[Tournament].reads(json)
    }

  }
case class TournamentWithSeries(tournament: Tournament, series: List[SeriesWithPlayers])

object TournamentResultModel{
  implicit object TournamentIsCrudable extends Crudable[Tournament]{
    override def getId(crudable: Tournament): String = crudable.id
    override implicit val getResult: GetResult[Tournament] = GetResult(r => Tournament(r.<<,r.<<,r.nextDate().toLocalDate,r.<<,r.<<,r.<<))
  }
}