package repositories.numongo.repos

import com.google.inject.Inject
import models.player.{Player, SeriesPlayer, SeriesPlayerFormat}
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument
import repositories.numongo.GenericMongoRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesPlayerRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[SeriesPlayer, String]("series_players", SeriesPlayerFormat.reads, SeriesPlayerFormat.writes) {
  def getAllSeriesPlayers(seriesId: String) = findAllByField("seriesId", seriesId)

  def subscribe(seriesPlayer: SeriesPlayer): Future[Option[SeriesPlayer]] = create(seriesPlayer).map(seriesPlayer => Some(seriesPlayer))

  def deleteSubscriptions(seriesId: String, player: Player) = {
    findAllByJsQuery(Json.obj("seriesId" -> seriesId, "player.id" -> player.id))
      .flatMap{seriesPlayers =>
      Future.sequence(seriesPlayers.map(player => delete(player.id)))
    }
  }.map(_.length)

  def getSubscriptionsOfPlayerInTournament(playerId: String, seriesId: String) =
    findAllByJsQuery(Json.obj("seriesId" -> seriesId, "player.id" -> playerId))



  def retrieveAllSeriesPlayers(seriesId: String): Future[List[SeriesPlayer]] = findAllByField("seriesId", seriesId)

}