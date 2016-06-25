package models.player

import models.Crudable
import slick.jdbc.GetResult
import utils.RankConverter

case class SeriesPlayerWithScores(id: String, playerId: String, seriesId: String, firstname: String, lastname: String, rank: Rank,wonMatches: Int=0, lostMatches: Int=0, wonSets: Int=0, lostSets: Int=0, wonPoints: Int=0, lostPoints: Int=0, totalPoints: Int=0)

object SeriesPlayerWithScoresEvidence{
  implicit object SeriesPlayerWithScoresIsCrudable extends Crudable[SeriesPlayerWithScores]{
    override def getId(crudable: SeriesPlayerWithScores): String = crudable.id
    override implicit val getResult: GetResult[SeriesPlayerWithScores] = GetResult(r => SeriesPlayerWithScores(r.<<,r.<<,r.<<,r.<<,r.<<,RankConverter.getRankOfInt(r.<<),r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<))
  }
}
