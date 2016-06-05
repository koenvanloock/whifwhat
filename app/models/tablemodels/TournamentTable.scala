package models.tablemodels

import java.sql.Date
import java.time.LocalDate

import models.Tournament
import slick.driver.MySQLDriver.api.{TableQuery => _, _}

class TournamentTable(tag: Tag) extends Table[Tournament](tag, "TOURNAMENTS"){
  implicit val myDateColumnType = MappedColumnType.base[java.time.LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )

  def id = column[String]("ID", O.PrimaryKey, O.Length(100))
  def name = column[String]("TOURNAMENT_NAME")
  def date = column[LocalDate]("TOURNAMENT_DATE")
  def maxNumberOfSeriesEntries = column[Int]("MAX_NUMBER_OF_SERIESENTRIES")
  def hasMultipleSeries = column[Boolean]("HAS_MULTIPLE_SERIES")
  def showClub = column[Boolean]("SHOW_CLUB")
  def * = (id, name, date, maxNumberOfSeriesEntries, hasMultipleSeries, showClub) <>((Tournament.apply _).tupled, Tournament.unapply)
}

  object TournamentTableTableModel {

    implicit object userTableTableModel extends TableModel[TournamentTable] {
      override def getRepId(tm: TournamentTable): Rep[String] = tm.id
    }

}
