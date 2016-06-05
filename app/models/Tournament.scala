package models

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import slick.jdbc.GetResult


case class Tournament(tournamentId: String, tournamentName: String, tournamentDate: LocalDate, maximumNumberOfSeriesEntries: Int, hasMultipleSeries: Boolean, showClub: Boolean) extends Crudable[Tournament]{
  override def getId(tournament: Tournament): String = tournament.tournamentId

  override implicit val getResult: GetResult[Tournament] = GetResult(r => Tournament(r.<<,r.<<,r.nextDate().toLocalDate,r.<<,r.<<,r.<<))
}

object TournamentResultModel{
  implicit object TournamentIsCrudable extends Crudable[Tournament]{
    override def getId(crudable: Tournament): String = crudable.tournamentId
    override implicit val getResult: GetResult[Tournament] = GetResult(r => Tournament(r.<<,r.<<,r.nextDate().toLocalDate,r.<<,r.<<,r.<<))
  }
}