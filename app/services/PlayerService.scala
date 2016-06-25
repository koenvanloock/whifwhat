package services

import java.util.UUID
import javax.inject.Inject

import com.typesafe.scalalogging.LazyLogging
import models.player.{SeriesPlayer, Ranks, Player}
import repositories.{SeriesRepository, SeriesPlayerRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayerService @Inject()(seriesPlayerRepository: SeriesPlayerRepository, seriesRepository: SeriesRepository) extends GenericAtomicCrudService[Player] with LazyLogging{
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


  val players =List(
    Player("1", "Koen","Van Loock", Ranks.D0 ),
    Player("2", "Hans","Van Bael", Ranks.E4 ),
    Player("3", "Luk","Geraets", Ranks.D6 ),
    Player("4", "Lode","Van Renterghem", Ranks.E6 ),
    Player("5", "Tim","Firquet", Ranks.C2 ),
    Player("6", "Aram","Pauwels", Ranks.B4 ),
    Player("7", "Tim","Uitdewilligen", Ranks.E0 ),
    Player("8", "Matthias","Lesuise", Ranks.D6 ),
    Player("9", "Gil","Corrujeira-Figueira", Ranks.D0 )
  )


  def getPlayers = Future(players)

  def getPlayer(playerId: String):Future[Option[Player]] = {
    Future(players.find(player => player.playerId.contains(playerId)))
  }

  def createPlayer(player: Player): Future[Option[Player]] = super.create(player)

  def updatePlayer(player: Player) = super.update(player)

  def deletePlayer(playerId: String) = super.delete(playerId)
}
