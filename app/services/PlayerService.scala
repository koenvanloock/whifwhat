package services

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import models.player.{Player, SeriesPlayer}
import play.api.libs.json.Json
import repositories.numongo.repos.{PlayerRepository, SeriesPlayerRepository, SeriesRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayerService @Inject()(seriesPlayerRepository: SeriesPlayerRepository, seriesRepository: SeriesRepository, playerRepository: PlayerRepository) extends LazyLogging{

  def getSeriesOfPlayer(playerId: String, tournamentId: String) =
    seriesRepository.findAllByField("tournamentId", tournamentId).flatMap{ seriesList =>
      Future.sequence(seriesList.map { series =>
        seriesPlayerRepository.getSubscriptionsOfPlayerInTournament(playerId, series.id).map( subscriptions => if(subscriptions.isEmpty) None else Some(series))
      })
    }.map(_.flatten)

  def findByString(search: String): Future[List[Player]] = Future.sequence{

    List(
      Json.obj("firstname" -> Json.obj("$regex" ->  (".*" + search + ".*"), "$options" -> "-i")),
      Json.obj("firstname" -> Json.obj("$regex" ->  ("^" + search + ".*"), "$options" -> "-i")),
      Json.obj("lastname" -> Json.obj("$regex" ->  (".*" + search + ".*"), "$options" -> "-i")),
      Json.obj("lastname" -> Json.obj("$regex" ->  ("^" + search + ".*"), "$options" -> "-i")),
      Json.obj("rank.name" -> Json.obj("$regex" ->  ("^" + search + ".*"), "$options" -> "-i"))
    ).map{
      jsObj => playerRepository.findFirstByJsQuery(jsObj)
    }
  }.map(_.flatten.distinct)

  def deleteSubscriptions(seriesId: String, player: Player): Future[Int] = { seriesPlayerRepository.deleteSubscriptions(seriesId, player).map{ numberOfDeletedRows =>
      println(s"subscriptions deleted for $seriesId: "+numberOfDeletedRows.toString)
      numberOfDeletedRows
  }}

  def subscribe: (SeriesPlayer) => Future[Option[SeriesPlayer]] = seriesPlayer => seriesPlayerRepository.subscribe(seriesPlayer)

  def getSeriesPlayers(seriesId: String) = seriesPlayerRepository.getAllSeriesPlayers(seriesId)

  def getPlayers = playerRepository.findAll()

  def getPlayer(playerId: String):Future[Option[Player]] = playerRepository.findById(playerId)

  def createPlayer(player: Player): Future[Player] = playerRepository.create(player)

  def updatePlayer(player: Player) = playerRepository.update(player)

  def deletePlayer(playerId: String) = playerRepository.delete(playerId)
}
