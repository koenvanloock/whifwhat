package models

import models.player.SeriesPlayer
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
                             currentRoundNr: Int=1,
                             tournamentId: String)

case class SeriesWithPlayers(seriesId: String,
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
                             currentRoundNr: Int=1,
                             seriesPlayers: List[SeriesPlayer],
                             tournamentId: String)

object TournamentSeriesResultModel{
  implicit object TournamentSeriesIsCrudable extends Crudable[TournamentSeries]{
    override def getId(crudable: TournamentSeries): String = crudable.seriesId

    override implicit val getResult: GetResult[TournamentSeries] = GetResult(r => TournamentSeries(r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<,r.<<))
  }
}