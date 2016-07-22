package models.tablemodels

import models.Crudable
import models.player._
import slick.jdbc.GetResult
import slick.lifted.Tag
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.lifted.TableQuery
import utils.RankConverter
import scala.language.postfixOps

class RoundPlayerTable (tag: Tag) extends Table[SeriesRoundPlayerWithScores](tag, "ROUND_PLAYERS") {

  implicit def rankColumnType = MappedColumnType.base[Rank, Int](
    rank => rank.value,
    rankValue => RankConverter.getRankOfInt(rankValue)
  )

  def id = column[String]("ID", O.PrimaryKey, O.Length(50))
  def seriesPlayerId = column[String]("SERIES_PLAYER_ID")
  def seriesRoundId = column[String]("SERIES_ROUND_ID")

  def firstname = column[String]("FIRSTNAME")
  def lastname=  column[String]("LASTNAME")
  def rank = column[Rank]("TOURNAMENT_RANK")

  def wonMatches = column[Int]("WON_MATCHES")
  def lostMatches = column[Int]("LOST_MATCHES")

  def wonSets = column[Int]("WON_SETS")
  def lostSets = column[Int]("LOST_SETS")

  def wonPoints = column[Int]("WON_POINTS")
  def lostPoints = column[Int]("LOST_POINTS")

  def totalPoints = column[Int]("TOTAL_POINTS")

  def * = (id, seriesPlayerId, seriesRoundId, firstname, lastname, rank, wonMatches, lostMatches, wonSets, lostSets, wonPoints, lostPoints, totalPoints) <>((SeriesRoundPlayerWithScores.apply _).tupled, SeriesRoundPlayerWithScores.unapply)
}

object RoundPlayerTableTableModel {

  implicit object roundPlayerTableEvidence extends TableModel[RoundPlayerTable] {
    override def getRepId(tm: RoundPlayerTable): Rep[String] = tm.id
  }

}
