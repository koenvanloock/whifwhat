package models.player

import models.Crudable
import slick.jdbc.GetResult
import utils.RankConverter

/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 16:32
  */
case class Player(playerId: String, firstname: String, lastname: String, rank: Rank) extends Crudable[Player]{
  override def getId(player: Player): String = player.playerId

  override implicit val getResult: GetResult[Player] = GetResult(r => Player(r.<<,r.<<,r.<<, RankConverter.getRankOfInt(r.<<)))
}
