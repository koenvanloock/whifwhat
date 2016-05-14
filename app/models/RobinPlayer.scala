package models

case class RobinPlayer(robinPlayerId: String, robinGroupId: String, seriesPlayerId: String, firstname: String, lastname: String, rank: Rank, robinNr: Int, wonMatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int) extends Crudable[RobinPlayer]{
  override def getId: String = robinPlayerId

  override def setId(newId: String): RobinPlayer = this.copy(robinPlayerId = newId)
}

