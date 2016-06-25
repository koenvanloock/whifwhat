package utils


import java.time.LocalDate
import java.util.UUID

import models.player.{Rank, PlayerScores, SeriesPlayer}
import models.{SeriesWithPlayers, TournamentWithSeries, TournamentSeries, Tournament}
import play.api.Logger
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{Request, AnyContent}
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax


object JsonUtils {
  val seriesPlayerInitReads: Reads[SeriesPlayer] = (
    (JsPath \ "playerId").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "seriesId").read[String] and
      (JsPath \ "firstname").read[String] and
      (JsPath \ "lastname").read[String] and
      (JsPath \ "rank").read[Rank](Json.reads[Rank])
    ) (SeriesPlayer.apply(UUID.randomUUID().toString,_, _, _, _, _, PlayerScores()))


  object ListWrites {
    def listToJson[W: Writes]: List[W] => JsValue = { ws =>
      Json.toJson(ws.map(Json.toJson(_)))
    }

    implicit class JsonOps(jsonObject: Json.type) {
      def listToJson[W: Writes](ws: List[W]) = ListWrites.listToJson.apply(ws)
    }

  }

  def parseRequestBody[T](request: Request[AnyContent])(implicit reads: Reads[T]): Option[T] = {
      request.body.asJson.flatMap( json => {
        println(json.toString())
        json.validate[T](reads).asOpt})
  }

  implicit val localDateReads: Reads[LocalDate] = (
      (JsPath \ "year").read[Int] and
      (JsPath \ "month").read[Int] and
    (JsPath \ "day").read[Int]
    )(LocalDate.of(_,_,_))

  val tournamentReads: Reads[Tournament] = (
    (JsPath \ "tournamentName").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "tournamentDate").read[LocalDate](localDateReads) and
      (JsPath \ "maximumNumberOfSeriesEntries").read[Int] and
      (JsPath \ "hasMultipleSeries").read[Boolean] and
      (JsPath \ "showClub").read[Boolean]
    ) (Tournament.apply(UUID.randomUUID().toString,_, _, _, _, _))

  val seriesReads: Reads[TournamentSeries] = (
    (JsPath \ "seriesName").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "seriesColor").read[String] and
      (JsPath \ "setTargetScore").read[Int] and
      (JsPath \ "numberOfSetsToWin").read[Int] and
      (JsPath \ "playingWithHandicaps").read[Boolean] and
      (JsPath \ "extraHandicapForRecs").read[Int] and
      (JsPath \ "showReferees").read[Boolean] and
      (JsPath \ "tournamentId").read[String]
    ) (TournamentSeries.apply(UUID.randomUUID().toString,_, _, _, _, _, _, _, 1, _))


  val tournamentWithSeriesWrites: Writes[TournamentWithSeries] = new Writes[TournamentWithSeries] {
    override def writes(o: TournamentWithSeries): JsValue = Json.obj(
      "tournamentId" -> o.tournament.tournamentId,
      "tournamentName" -> o.tournament.tournamentName,
      "tournamentDate" -> o.tournament.tournamentDate,
      "showClub" -> o.tournament.showClub,
      "hasMultipleSeries" -> o.tournament.hasMultipleSeries,
      "maximumNumberOfSeriesEntries" -> o.tournament.maximumNumberOfSeriesEntries,
      "series" -> o.series.map(Json.toJson(_)(seriesWithPlayersWrites))
    )
  }

  val seriesWithPlayersWrites : Writes[SeriesWithPlayers] = new Writes[SeriesWithPlayers] {
    implicit val rankWrites = Json.writes[Rank]
    implicit val scoreWrites = Json.writes[PlayerScores]
    override def writes(o: SeriesWithPlayers): JsValue = Json.obj(
      "seriesId" -> o.seriesId,
      "seriesName" -> o.seriesName,
      "seriesColor" -> o.seriesColor,
     "setTargetScore" -> o.setTargetScore,
      "numberOfSetstoWin" -> o.numberOfSetsToWin,
    "playingWithHandicaps" -> o.playingWithHandicaps,
    "extraHandicapForRecs" -> o.extraHandicapForRecs,
    "showReferees" -> o.showReferees,
    "seriesPlayers" -> o.seriesPlayers.map{ Json.toJson(_)(Json.writes[SeriesPlayer])},
    "tournamentId" -> o.tournamentId
    )
  }
}
