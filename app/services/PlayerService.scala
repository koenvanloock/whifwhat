package services

import java.util.UUID

import models.player.{Ranks, Player}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlayerService extends GenericAtomicCrudService[Player]{

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
