package models.matches

import models.Crudable
import slick.jdbc.GetResult


case class SiteMatch(
                      matchId: String,
                      playerA: String,
                      playerB: String,
                      roundId: String,
                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int,
                      wonSetsA: Int,
                      wonSetsB: Int)

object MatchEvidence{
  implicit object MatchIsCrudable extends Crudable[SiteMatch]{
    override def getId(crudable: SiteMatch): String = crudable.matchId
    override implicit val getResult: GetResult[SiteMatch] = GetResult(r => SiteMatch(r.<<,r.<<,r.<<,r.<<, r.<<, r.<<, r.<<, r.<<, r.<<, r.<<))
  }
}
