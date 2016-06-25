package models

import slick.jdbc.GetResult

sealed trait SeriesRound{
  def getId: String
}

case class SiteBracketRound(seriesRoundId: String, numberOfBracketRounds: Int, seriesId: String, roundNr: Int) extends SeriesRound {
  override def getId = seriesRoundId
}
case class SiteRobinRound(seriesRoundId: String, numberOfRobins: Int, seriesId: String, roundNr: Int) extends SeriesRound {
  override def getId: String = seriesRoundId
}

case class GenericSeriesRound(seriesRoundId: String, numberOfBracketRounds: Option[Int], numberOfRobins: Option[Int], roundType: String, seriesId: String, roundNr: Int)
object GenericSeriesRoundIsCrudable{
  implicit object seriesRoundIsCrudable extends Crudable[GenericSeriesRound]{
    override def getId(crudable: GenericSeriesRound): String =  crudable.seriesRoundId

    override implicit val getResult: GetResult[GenericSeriesRound] = GetResult(r => GenericSeriesRound(r.<<,r.<<,r.<<,r.<<,r.<<,r.<<))
  }
}
