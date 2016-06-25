package models.tablemodels

import models.GenericSeriesRound
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import slick.lifted.{Tag, TableQuery}
import scala.language.postfixOps

class GenericSeriesRoundTable(tag: Tag) extends Table[GenericSeriesRound](tag, "SERIES_ROUNDS") {

  def id = column[String]("SERIES_ROUND_ID", O.PrimaryKey, O.Length(100))

  def numberOfBrackets = column[Option[Int]]("NUMBER_OF_BRACKETS")

  def numberOfRobins = column[Option[Int]]("NUMBER_OF_ROBINS")

  def roundType = column[String]("ROUND_TYPE")

  def seriesId = column[String]("SERIES_ID")

  def roundNr = column[Int]("ROUND_NR")


  def * = (id, numberOfBrackets, numberOfRobins, roundType, seriesId, roundNr) <>((GenericSeriesRound.apply _).tupled, GenericSeriesRound.unapply)
}

object GenericSeriesRoundTableModel {

  implicit object userTableTableModel extends TableModel[GenericSeriesRoundTable] {
    override def getRepId(tm: GenericSeriesRoundTable): Rep[String] = tm.id
  }

}
