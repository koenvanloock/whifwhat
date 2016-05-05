package models

case class RobinPlayer(robinPlayerId: String, robinGroupId: String, seriesPlayerId: String, rankValue: Int, robinNr: Int, wonMatches: Int, lostMatches: Int, wonSets: Int, lostSets: Int, wonPoints: Int, lostPoints: Int) extends Crudable[RobinPlayer]{
  override def getId: String = robinPlayerId

  override def setId(newId: String): RobinPlayer = this.copy(robinPlayerId = newId)
}

