package models.player

import models.Crudable
import slick.jdbc.GetResult
import utils.RankConverter

case class RobinPlayer(robinPlayerId: String, robinGroupId: String, seriesPlayerId: String, firstname: String, lastname: String, rank: Rank, robinNr: Int, playerScores: PlayerScores) extends Crudable[RobinPlayer]{
  override def getId(robinPlayer: RobinPlayer): String = robinPlayer.robinPlayerId

  //@TODO implement getResult of playerScores
  override implicit val getResult: GetResult[RobinPlayer] = GetResult(r => RobinPlayer(r.<<,r.<<,r.<<,r.<<,r.<<,RankConverter.getRankOfInt(r.<<),r.<<,PlayerScores()))
}
