package models.matches

import models.Crudable


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
  override def getId: String = matchId

  override def setId(newId: String): SiteMatch = this.copy(matchId = newId)
}
