package models

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

case class SiteMatchWithGames(
                      matchId: String,
                      playerA: String,
                      playerB: String,
                      roundId: String,
                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int,
                      wonSetsA: Int,
                      wonSetsB: Int,
                      sets: List[SiteGame])