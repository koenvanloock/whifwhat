package services

import javax.inject.Inject

import models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundService @Inject()(matchService: MatchService) extends GenericAtomicCrudService[GenericSeriesRound]{

  val seriesRounds: List[SeriesRound] = List(
    SiteRobinRound("1",2,"1",1),
    SiteBracketRound("2",2,"1",1)
  )

  def getSeriesRound(seriesRoundId: String) =  {
    Future(seriesRounds.find(round => round.getId.contains(seriesRoundId)))
  }


  def isRoundComplete(seriesRoundWithPlayersAndMatches : SeriesRoundWithPlayersAndMatches): Boolean = seriesRoundWithPlayersAndMatches match{
    case robinRound:RobinRound =>
      robinRound.robinList.forall( robinGroup => robinGroup.robinMatches.forall(matchService.isMatchComplete))
    case bracketRound: Bracket =>
       bracketRound.bracketRounds.forall( bracketMatchList => bracketMatchList.forall(matchService.isBracketMatchComplete))
  }
}
