package models.tablemodels

import models.TournamentSeries
import slick.lifted.Tag
import play.api.libs.functional.syntax._
import play.api.libs.json._
import slick.jdbc.GetResult
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.lifted.TableQuery
import scala.language.postfixOps

class SeriesTable(tag: Tag) extends Table[TournamentSeries](tag, "TOURNAMENT_SERIES") {
  def id = column[String]("ID", O.PrimaryKey, O.Length(100))
  def name = column[String]("SERIES_NAME")
  def seriesColor = column[String]("PASSWORD")
  def numberOfSetsToWin = column[Int]("NUMBER_OF_SETS_TO_WIN")
  def setTargetScore = column[Int]("SET_TARGET_SCORE")
  def playingWithHandicaps = column[Boolean]("PLAYING_WITH_HANDICAPS")
  def extraHandicapForRecs = column[Int]("EXTRA_HANDICAP_FOR_RECS")
  def showReferees= column[Boolean]("SHOW_REFEREES")
  def currentRoundNr= column[Int]("CURRENT_ROUND_NR")
  def tournamentId = column[String]("TOURNAMENT_ID")

    def * = (id, name, seriesColor, numberOfSetsToWin, setTargetScore, playingWithHandicaps, extraHandicapForRecs, showReferees, currentRoundNr, tournamentId) <>(TournamentSeries.tupled, TournamentSeries.unapply)
  }

  object SeriesTableTableModel {

    implicit object userTableTableModel extends TableModel[SeriesTable] {
      override def getRepId(tm: SeriesTable): Rep[String] = tm.id
    }
}
