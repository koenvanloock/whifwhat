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
                      wonSetsB: Int) extends Crudable[SiteMatch]{
  override def getId(siteMatch: SiteMatch): String = siteMatch.matchId
  override implicit val getResult: GetResult[SiteMatch] = GetResult(r => SiteMatch(r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<))
}
