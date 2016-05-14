package models

/**
  * @author Koen Van Loock
  * @version 1.0 24/04/2016 1:31
  */
sealed trait SeriesRound{
  def getId: String
}

case class SiteBracketRound(seriesRoundId: String, numberOfBracketRounds: Int, seriesId: String, roundNr: Int) extends SeriesRound {
  override def getId = seriesRoundId
}
case class SiteRobinRound(seriesRoundId: String, numberOfRobins: Int, seriesId: String, roundNr: Int) extends SeriesRound {
  override def getId: String = seriesRoundId
}

case class GenericSeriesRound(seriesRoundId: String, numberOfBracketRounds: Option[Int], numberOfRobins: Option[Int], roundType: String, seriesId: String, roundNr: Int) extends Crudable[GenericSeriesRound]{
  override def getId: String = seriesRoundId

  override def setId(newId: String): GenericSeriesRound = this.copy(seriesRoundId = newId)
}
