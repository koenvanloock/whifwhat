package models.matches

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
