package models

/**
  * @author Koen Van Loock
  * @version 1.0 24/04/2016 1:31
  */
sealed trait SeriesRound

case class SiteBracketRound(seriesRoundId: Option[String], numberOfBracketRounds: Int, roundType: String, seriesId: String, roundNr: Int) extends SeriesRound
case class RobinRound(seriesRoundId: Option[String], numberOfRobins: Int, roundType: String,seriesId: String, roundNr: Int) extends SeriesRound

case class GenericSeriesRound(seriesRoundId: Option[String], numberOfBracketRounds: Option[Int], numberOfRobins: Option[Int], roundType: String, seriesId: String, roundNr: Int) extends Crudable[GenericSeriesRound]{
  override def getId: String = seriesRoundId.get

  override def setId(newId: String): GenericSeriesRound = this.copy(seriesRoundId = Some(newId))
}
