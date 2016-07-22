package repositories

import javax.inject.Inject

import models.player.{BracketPlayer, SeriesRoundPlayerWithScores, SeriesRoundPlayer}
import models.tablemodels.RoundPlayerTable
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.TableQuery

import scala.concurrent.Future

import models.player.RoundPlayerWithScoresEvidence._
import models.tablemodels.RoundPlayerTableTableModel._

import scala.concurrent.ExecutionContext.Implicits.global

class RoundPlayerRepository  @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[SeriesRoundPlayerWithScores, RoundPlayerTable](dbConfigProvider = dbConfigProvider)("ROUND_PLAYERS"){
  override def query: driver.api.TableQuery[RoundPlayerTable] = TableQuery[RoundPlayerTable]
  import driver.api._


  def convertRobinPlayerToRoundPlayerWithScores: (SeriesRoundPlayer) => SeriesRoundPlayerWithScores = roundPlayer => SeriesRoundPlayerWithScores(roundPlayer.id, roundPlayer.seriesPlayerId, roundPlayer.seriesRoundId, roundPlayer.firstname, roundPlayer.lastname, roundPlayer.rank, roundPlayer.playerScores.wonMatches, roundPlayer.playerScores.lostMatches, roundPlayer.playerScores.wonSets, roundPlayer.playerScores.lostSets, roundPlayer.playerScores.wonPoints, roundPlayer.playerScores.lostPoints, roundPlayer.playerScores.totalPoints)

  def saveRobinPlayers(seriesPlayers: List[SeriesRoundPlayer]): Future[List[SeriesRoundPlayerWithScores]] = {
    Future.sequence(seriesPlayers.map(convertRobinPlayerToRoundPlayerWithScores).map(create)).map(_.flatten)
  }

  def convertBracketPlayerToRoundPlayerWithScores: (BracketPlayer) => SeriesRoundPlayerWithScores = bracketPlayer => SeriesRoundPlayerWithScores(bracketPlayer.id, bracketPlayer.bracketId, bracketPlayer.playerId, bracketPlayer.firstname, bracketPlayer.lastname, bracketPlayer.rank, bracketPlayer.playerScores.wonMatches, bracketPlayer.playerScores.lostMatches, bracketPlayer.playerScores.wonSets, bracketPlayer.playerScores.lostSets, bracketPlayer.playerScores.wonPoints, bracketPlayer.playerScores.lostPoints, bracketPlayer.playerScores.totalPoints)


  def saveBracketPlayers(seriesPlayers: List[BracketPlayer]): Future[List[SeriesRoundPlayerWithScores]] = {
    Future.sequence(seriesPlayers.map(convertBracketPlayerToRoundPlayerWithScores).map(create)).map(_.flatten)
  }

}
