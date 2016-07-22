package services

import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import models.player.{SeriesPlayer, Ranks, Player}
import repositories.{PlayerRepository, SeriesRepository, SeriesPlayerRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayerService @Inject()(seriesPlayerRepository: SeriesPlayerRepository, seriesRepository: SeriesRepository, playerRepository: PlayerRepository) extends LazyLogging{
  def getSeriesOfPlayer(playerId: String, tournamentId: String) =
    seriesRepository.retrieveAllByField("TOURNAMENT_ID", tournamentId).flatMap{ seriesList =>
      Future.sequence(seriesList.map { series =>
        seriesPlayerRepository.getSubscriptionsOfPlayerInTournament(playerId, series.seriesId).map( subscriptions => if(subscriptions.isEmpty) None else Some(series))
      })
    }.map(_.flatten)

  def deleteSubscriptions(seriesId: String, playerId: String): Future[Int] = { seriesPlayerRepository.deleteSubscriptions(seriesId, playerId).map{ numberOfDeletedRows =>
      println(s"subscriptions deleted for $seriesId: "+numberOfDeletedRows.toString)
      numberOfDeletedRows
  }}

  def subscribe: (SeriesPlayer) => Future[Option[SeriesPlayer]] = seriesPlayer => seriesPlayerRepository.subscribe(seriesPlayer)

  def getSeriesPlayers(seriesId: String) = seriesPlayerRepository.getAllSeriesPlayers(seriesId)

  def getPlayers = playerRepository.retrieveAll()

  def getPlayer(playerId: String):Future[Option[Player]] = playerRepository.retrieveById(playerId)

  def createPlayer(player: Player): Future[Option[Player]] = playerRepository.create(player)

  def updatePlayer(player: Player) = playerRepository.update(player)

  def deletePlayer(playerId: String) = playerRepository.delete(playerId)
}
