package models.player

import models.Crudable

/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 16:32
  */
case class Player(playerId: String, firstname: String, lastname: String, rank: Rank) extends Crudable[Player]{
  override def getId: String = playerId

  override def setId(newId: String): Player = this.copy(playerId = newId)
}
