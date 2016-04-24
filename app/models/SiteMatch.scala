package models

case class SiteMatch(
                      matchId: Option[String],
                      playerA: String,
                      playerB: String,

                      handicap: Int,
                      isHandicapForB: Boolean,
                      targetScore: Int,
                      numberOfSetsToWin: Int) extends Crudable[SiteMatch]{
  override def getId: String = matchId.get

  override def setId(newId: String): SiteMatch = this.copy(matchId = Some(newId))
}
