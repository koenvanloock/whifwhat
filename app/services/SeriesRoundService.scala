package services

import models.{SeriesRound, SiteBracketRound, RobinRound, GenericSeriesRound}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundService extends GenericAtomicCrudService[GenericSeriesRound]{

  val seriesRounds: List[SeriesRound] = List(
    RobinRound(Some("1"),2,"1",1),
    SiteBracketRound(Some("2"),2,"1",1)
  )

  def getSeriesRound(seriesRoundId: String) =  {
    Future(seriesRounds.find(round => round.getId.contains(seriesRoundId)))
  }

}
