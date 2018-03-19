package models

import java.time.LocalDate
import play.api.libs.json._


case class Tournament(id: String, tournamentName: String, tournamentDate: LocalDate, maximumNumberOfSeriesEntries: Int, hasMultipleSeries: Boolean, showClub: Boolean)

  object TournamentEvidence extends Evidence[Tournament] {
      override def getId(tournament: Tournament): Option[String] = Some(tournament.id)

      override def setId(newId: String)(tournament: Tournament): Tournament = tournament.copy(id = tournament.id)

      override def writes(o: Tournament): JsObject = Json.format[Tournament].writes(o)

      override def reads(json: JsValue): JsResult[Tournament] = Json.format[Tournament].reads(json)

  }
case class TournamentWithSeries(tournament: Tournament, series: List[SeriesWithPlayers])