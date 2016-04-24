package models

/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 16:32
  */
case class Player(playerId: Option[String], firstname: String, lastname: String, rank: Rank) extends Crudable[Player]{
  override def getId: String = playerId.get

  override def setId(newId: String): Player = this.copy(playerId = Some(newId))
}
