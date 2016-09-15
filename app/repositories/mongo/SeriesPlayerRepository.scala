package repositories.mongo

import javax.inject.Inject

import models.player.{SeriesPlayer, SeriesPlayerEvidence}
import play.modules.reactivemongo.ReactiveMongoApi
import SeriesPlayerEvidence._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesPlayerRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[SeriesPlayer]("series_players","asc",10){
  def getAllSeriesPlayers(seriesId: String) = retrieveAllByField("seriesId", seriesId)

  def subscribe(seriesPlayer: SeriesPlayer): Future[Option[SeriesPlayer]] = create(seriesPlayer).map(seriesPlayer => Some(seriesPlayer))

  def deleteSubscriptions(seriesId: String, playerId: String) = {
    retrieveAllByFields(Some(Map("playerId" -> playerId, "seriesId" -> seriesId))).flatMap{seriesPlayers =>
      Future.sequence(seriesPlayers.map(player => delete(player.id)))
    }
  }.map(_.length)

  def getSubscriptionsOfPlayerInTournament(playerId: String, seriesId: String) = {
    retrieveAllByFields(Some(Map("playerId" -> playerId, "seriesId" -> seriesId)))
  }


  def retrieveAllSeriesPlayers(seriesId: String): Future[List[SeriesPlayer]] = {
    retrieveAllByField("seriesId", seriesId)
  }
}
