package models.player

import models.Crudable

case class RobinPlayer(robinPlayerId: String, robinGroupId: String, seriesPlayerId: String, firstname: String, lastname: String, rank: Rank, robinNr: Int, playerScores: PlayerScores) extends Crudable[RobinPlayer]{
  override def getId: String = robinPlayerId

  override def setId(newId: String): RobinPlayer = this.copy(robinPlayerId = newId)
}
