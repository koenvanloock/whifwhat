package repositories

import javax.inject.Inject

import models.player.{SeriesPlayerWithScoresEvidence, SeriesPlayerWithScores, PlayerScores, SeriesPlayer}
import models.tablemodels.{SeriesPlayerTableTableModel, SeriesPlayerTable}
import play.api.db.slick.DatabaseConfigProvider
import slick.lifted.TableQuery
import utils.RankConverter

import SeriesPlayerTableTableModel._
import SeriesPlayerWithScoresEvidence._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesPlayerRepository @Inject()(override protected val dbConfigProvider: DatabaseConfigProvider) extends GenericRepo[SeriesPlayerWithScores, SeriesPlayerTable](dbConfigProvider = dbConfigProvider)("SERIES_PLAYERS"){
  override def query: driver.api.TableQuery[SeriesPlayerTable] = TableQuery[SeriesPlayerTable]
  import driver.api._

  def getSubscriptionsOfPlayerInTournament(playerId: String, seriesId: String): Future[List[SeriesPlayer]] = {
    db.run(query.filter(_.playerId === playerId).filter(_.seriesId === seriesId).result).map(result =>
      result.map(convertSeriesPlayerWithScoresToSeriesPlayer).toList
    )
  }


  def deleteSubscriptions(seriesId: String, playerId: String): Future[Int] = {
    val q = query.filter(_.seriesId === seriesId).filter(_.playerId === playerId)
    db.run(q.delete)
  }

  def subscribe(seriesPlayer: SeriesPlayer): Future[Option[SeriesPlayer]] = create(convertSeriesPlayerToSeriesPlayerWithScores(seriesPlayer)).map {
    case Some(seriesWithScores) => Some(convertSeriesPlayerWithScoresToSeriesPlayer(seriesWithScores))
    case _ => None
  }

  def retrieveAllSeriesPlayers(seriesId: String) = {
    retrieveAllByField("SERIES_ID", seriesId).map(playerList => playerList.map(convertSeriesPlayerWithScoresToSeriesPlayer))
  }


  def getAllSeriesPlayers(seriesId: String): Future[List[SeriesPlayer]] = retrieveAllByField("SERIES_ID", seriesId).map{ playersAndScores =>
    playersAndScores.map( convertSeriesPlayerWithScoresToSeriesPlayer)
  }

  def convertSeriesPlayerWithScoresToSeriesPlayer: (SeriesPlayerWithScores) => SeriesPlayer = {
    playerAndScore =>
      SeriesPlayer(
        playerAndScore.id,
        playerAndScore.playerId,
        playerAndScore.seriesId,
        playerAndScore.firstname,
        playerAndScore.lastname,
        playerAndScore.rank,
        PlayerScores(
          playerAndScore.wonMatches,
          playerAndScore.lostMatches,
          playerAndScore.wonSets,
          playerAndScore.lostSets,
          playerAndScore.wonPoints,
          playerAndScore.lostPoints,
          playerAndScore.totalPoints))
  }

  def convertSeriesPlayerToSeriesPlayerWithScores(seriesPlayer: SeriesPlayer): SeriesPlayerWithScores = {
    SeriesPlayerWithScores(
      seriesPlayer.id,
      seriesPlayer.playerId,
      seriesPlayer.seriesId,
      seriesPlayer.firstname,
      seriesPlayer.lastname,
      seriesPlayer.rank,
      seriesPlayer.playerScores.wonMatches,
      seriesPlayer.playerScores.lostMatches,
      seriesPlayer.playerScores.wonSets,
      seriesPlayer.playerScores.lostSets,
      seriesPlayer.playerScores.wonPoints,
      seriesPlayer.playerScores.lostPoints)
  }
}
