package models.tablemodels

import models.Crudable
import models.player.{SeriesPlayerWithScores, Rank, SeriesPlayer}
import slick.jdbc.GetResult
import slick.lifted.Tag
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.lifted.TableQuery
import utils.RankConverter
import scala.language.postfixOps

class SeriesPlayerTable (tag: Tag) extends Table[SeriesPlayerWithScores](tag, "SERIES_PLAYERS") {

  implicit def rankColumnType = MappedColumnType.base[Rank, Int](
    rank => rank.value,
    rankValue => RankConverter.getRankOfInt(rankValue)
  )
  def id = column[String]("SERIES_PLAYER_ID", O.PrimaryKey, O.Length(100))
  def playerId = column[String]("PLAYER_ID")
  def seriesId = column[String]("SERIES_ID")

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



  def * = (id, playerId, seriesId, firstname, lastname, rank, wonMatches, lostMatches, wonSets, lostSets, wonPoints, lostPoints, totalPoints) <>((SeriesPlayerWithScores.apply _).tupled, SeriesPlayerWithScores.unapply)
}

object SeriesPlayerTableTableModel {

  implicit object userTableTableModel extends TableModel[SeriesPlayerTable] {
    override def getRepId(tm: SeriesPlayerTable): Rep[String] = tm.id
  }
}