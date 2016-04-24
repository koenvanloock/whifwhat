package models

import java.time.LocalDate

case class Tournament(tournamentId: Option[String], tournamentName: String, tournamentDate: LocalDate, maximumNumberOfSeriesEntries: Int, hasMultipleSeries: Boolean, showClub: Boolean) extends Crudable[Tournament]{
  override def getId: String = tournamentId.get

  override def setId(newId: String): Tournament = this.copy(tournamentId = Some(newId))
}

