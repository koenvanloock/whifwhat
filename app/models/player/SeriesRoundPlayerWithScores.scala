package models.player

import models.Crudable
import slick.jdbc.GetResult
import utils.RankConverter

case class SeriesRoundPlayerWithScores(id: String, seriesPlayerId: String, seriesRoundId: String, firstname: String, lastname: String, rank: Rank,wonMatches: Int=0, lostMatches: Int=0, wonSets: Int=0, lostSets: Int=0, wonPoints: Int=0, lostPoints: Int=0, totalPoints: Int=0)

object RoundPlayerWithScoresEvidence{
  implicit object RoundPlayerWithScoresIsCrudable extends Crudable[SeriesRoundPlayerWithScores]{
    override def getId(crudable: SeriesRoundPlayerWithScores): String = crudable.id
    override implicit val getResult: GetResult[SeriesRoundPlayerWithScores] = GetResult(r => SeriesRoundPlayerWithScores(r.<<,r.<<,r.<<,r.<<,r.<<,RankConverter.getRankOfInt(r.<<),r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<))
  }
}