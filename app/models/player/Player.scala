package models.player

import models.Crudable
import slick.jdbc.GetResult
import utils.RankConverter

case class Player(playerId: String, firstname: String, lastname: String, rank: Rank, imagepath: Option[String]=None)

object PlayerEvidence{
  implicit object PlayerIsCrudable extends Crudable[Player]{
    override def getId(crudable: Player): String = crudable.playerId
    override implicit val getResult: GetResult[Player] = GetResult(r => Player(r.<<,r.<<,r.<<,RankConverter.getRankOfInt(r.<<),r.<<))
  }
}
