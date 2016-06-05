package models

import slick.jdbc.GetResult

case class TournamentSeries(
                             seriesId: String,
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
                             currentRoundNr: Int,
                             tournamentId: String) extends Crudable[TournamentSeries]{
  override def getId(tournamentSeries: TournamentSeries): String = tournamentSeries.seriesId

  override implicit val getResult: GetResult[TournamentSeries] = GetResult(r => TournamentSeries(r.<<, r.<<,r.<<, r.<<,r.<<, r.<<,r.<<, r.<<,r.<<, r.<<))
}