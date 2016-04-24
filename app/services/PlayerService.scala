package services

import java.util.UUID

import models.{Ranks, Player}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayerService extends GenericAtomicCrudService[Player]{

  val players =List(
    Player(None, "Koen","Van Loock", Ranks.D0 ),
    Player(None, "Hans","Van Bael", Ranks.E4 ),
    Player(None, "Luk","Geraets", Ranks.D6 ),
    Player(None, "Lode","Van Renterghem", Ranks.E6 ),
    Player(None, "Tim","Firquet", Ranks.C2 ),
    Player(None, "Aram","Pauwels", Ranks.B4 ),
    Player(None, "Tim","Uitdewilligen", Ranks.E0 ),
    Player(None, "Matthias","Lesuise", Ranks.D6 ),
    Player(None, "Gil","Corrujeira-Figueira", Ranks.D0 )
  )


  def getPlayers = Future(players)

  def getPlayer(playerId: String):Future[Option[Player]] = {
    Future(players.find(player => player.playerId.contains(playerId)))
  }

  def createPlayer(player: Player): Future[Option[Player]] = super.create(player)

  def updatePlayer(player: Player) = super.update(player)

  def deletePlayer(playerId: String) = super.delete(playerId)
}
