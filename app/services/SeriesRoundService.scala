package services

import models.{SeriesRound, SiteBracketRound, SiteRobinRound, GenericSeriesRound}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundService extends GenericAtomicCrudService[GenericSeriesRound]{

  val seriesRounds: List[SeriesRound] = List(
    SiteRobinRound("1",2,"1",1),
    SiteBracketRound("2",2,"1",1)
  )

  def getSeriesRound(seriesRoundId: String) =  {
    Future(seriesRounds.find(round => round.getId.contains(seriesRoundId)))
  }

}
