package models.tablemodels

import models.matches.SiteMatch
import models.player.{Player, Rank}
import slick.lifted.Tag
import slick.driver.MySQLDriver.api.{TableQuery => _, _}
import utils.RankConverter
import scala.language.postfixOps

class MatchTable(tag: Tag) extends Table[SiteMatch](tag, "MATCHES") {

  def id = column[String]("ID", O.PrimaryKey, O.Length(100))
  def playerA = column[String]("PLAYER_A")
  def playerB=  column[String]("PLAYER_B")
  def roundId = column[String]("ROUND_ID")
  def handicap = column[Int]("HANDICAP")
  def isHandicapForB = column[Boolean]("IS_HANDICAP_FOR_B")
  def setTargetScore = column[Int]("SET_TARGET_SCORE")
  def numberOfSetsToWin = column[Int]("NUMBER_OF_SETS_TO_WIN")
  def setsForA = column[Int]("SETS_FOR_A")
  def setsForB = column[Int]("SETS_FOR_B")



  def * = (id, playerA, playerB, roundId, handicap, isHandicapForB, setTargetScore, numberOfSetsToWin, setsForA, setsForB) <>((SiteMatch.apply _).tupled, SiteMatch.unapply)
}

object MatchTableTableModel {

  implicit object playerTableEvidence extends TableModel[MatchTable] {
    override def getRepId(tm: MatchTable): Rep[String] = tm.id
  }
}