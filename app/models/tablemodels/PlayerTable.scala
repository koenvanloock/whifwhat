package models.tablemodels

import models.player.{Player, Rank}
import slick.lifted.Tag
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import utils.RankConverter
import scala.language.postfixOps

class PlayerTable(tag: Tag) extends Table[Player](tag, "PLAYERS") {

  implicit def rankColumnType = MappedColumnType.base[Rank, Int](
    rank => rank.value,
    rankValue => RankConverter.getRankOfInt(rankValue)
  )
  def id = column[String]("ID", O.PrimaryKey, O.Length(100))
  def firstname = column[String]("FIRSTNAME")
  def lastname=  column[String]("LASTNAME")
  def rank = column[Rank]("RANK")
  def imagepath = column[Option[String]]("IMAGE_PATH")



  def * = (id, firstname, lastname, rank, imagepath) <>((Player.apply _).tupled, Player.unapply)
}

object PlayerTableTableModel {

  implicit object playerTableEvidence extends TableModel[PlayerTable] {
    override def getRepId(tm: PlayerTable): Rep[String] = tm.id
  }
}