package repositories.mongo

import javax.inject.Inject

import com.typesafe.scalalogging.StrictLogging
import models.player.{Player, SeriesPlayer, SeriesPlayerEvidence}
import SeriesPlayerEvidence._
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import play.modules.reactivemongo.json._
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesPlayerRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[SeriesPlayer]("series_players","asc",10) with StrictLogging{
  def getAllSeriesPlayers(seriesId: String) = retrieveAllByField("seriesId", seriesId)

  def subscribe(seriesPlayer: SeriesPlayer): Future[Option[SeriesPlayer]] = create(seriesPlayer).map(seriesPlayer => Some(seriesPlayer))

  def deleteSubscriptions(seriesId: String, player: Player) = {
      collectionFuture.flatMap{collection =>
      collection
        .find(BSONDocument("seriesId"-> seriesId, "player.id"-> player.id))
        .cursor[SeriesPlayer]()
        .collect[List]()}.flatMap{seriesPlayers =>
      Future.sequence(seriesPlayers.map(player => delete(player.id)))
    }
  }.map(_.length)

  def getSubscriptionsOfPlayerInTournament(playerId: String, seriesId: String) = {
    collectionFuture.flatMap{collection =>
      collection
        .find(BSONDocument("seriesId"-> seriesId, "player.id"-> playerId))
        .cursor[SeriesPlayer]()
        .collect[List]()}
  }


  def retrieveAllSeriesPlayers(seriesId: String): Future[List[SeriesPlayer]] = {
    logger.info("seriesId"+seriesId)
    retrieveAllByField("seriesId", seriesId)
  }
}
