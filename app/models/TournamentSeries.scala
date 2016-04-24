package models

/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 23:37
  */
case class TournamentSeries(
                             seriesId: Option[String],
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
                             currentRoundNr: Int,
                             tournamentId: String) extends Crudable[TournamentSeries]{
  override def getId: String = this.seriesId.get

  override def setId(newId: String): TournamentSeries = this.copy(seriesId = Some(newId))
}