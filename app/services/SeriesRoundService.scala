package services

import javax.inject.Inject


import models._
import models.player.{SeriesRoundPlayer, SeriesPlayerWithRoundPlayers, PlayerScores, SeriesPlayer}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundService @Inject()(matchService: MatchService) extends GenericAtomicCrudService[GenericSeriesRound]{

  def calculatePlayerScore: (SeriesPlayerWithRoundPlayers) => SeriesPlayer = {
    seriesPlayerWithRoundPlayers =>
      seriesPlayerWithRoundPlayers.seriesPlayer.copy(playerScores = seriesPlayerWithRoundPlayers.seriesRoundPlayers.map(_.playerScores).foldLeft(PlayerScores())(
        (accPlayerScore: PlayerScores, seriesRoundPlayerScore: PlayerScores) =>
           PlayerScores(
            accPlayerScore.wonMatches + seriesRoundPlayerScore.wonMatches,
            accPlayerScore.lostMatches + seriesRoundPlayerScore.lostMatches,
            accPlayerScore.wonSets + seriesRoundPlayerScore.wonSets,
            accPlayerScore.lostSets + seriesRoundPlayerScore.lostSets,
            accPlayerScore.wonPoints + seriesRoundPlayerScore.wonPoints,
            accPlayerScore.lostPoints + seriesRoundPlayerScore.lostPoints,
            accPlayerScore.totalPoints + seriesRoundPlayerScore.totalPoints)
      ))
  }

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
