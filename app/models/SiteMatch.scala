package models

case class SiteMatch(
                      matchId: String,
                      playerA: String,
                      playerB: String,

                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int) extends Crudable[SiteMatch]{
  override def getId: String = matchId

  override def setId(newId: String): SiteMatch = this.copy(matchId = newId)
}

case class SiteMatchWithGames(
                      matchId: String,
                      playerA: String,
                      playerB: String,

                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int,
                      sets: List[SiteGame])