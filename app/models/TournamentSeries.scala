package models

import models.player.SeriesPlayer
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}

case class TournamentSeries(
                             id: String,
                             seriesName: String,
                             seriesColor: String,
                             numberOfSetsToWin: Int,
                             setTargetScore: Int,
                             playingWithHandicaps: Boolean,
                             extraHandicapForRecs: Int,
                             showReferees: Boolean,
                             currentRoundNr: Int=1,
                             tournamentId: String)

  object SeriesEvidence extends Evidence[TournamentSeries] {
      override def getId(m: TournamentSeries): Option[String] = Some(m.id)

      override def setId(id: String)(m: TournamentSeries): TournamentSeries = m.copy(id = id)

      override def writes(o: TournamentSeries): JsObject = Json.format[TournamentSeries].writes(o)

      override def reads(json: JsValue): JsResult[TournamentSeries] = Json.format[TournamentSeries].reads(json)
  }

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
