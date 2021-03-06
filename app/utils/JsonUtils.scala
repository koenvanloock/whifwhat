package utils


import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

import models._
import models.matches.{PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Rank, SeriesPlayer}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{AnyContent, Request} // Combinator syntax


object JsonUtils {
  def listWrites[T](implicit tWrites: Writes[T]) = {
    new Writes[List[T]] {
      override def writes(list: List[T]): JsValue = Json.toJson(list.map(Json.toJson(_)(tWrites)))
    }
  }

  def listReads[T](implicit tReads: Reads[T]) = {
    new Reads[List[T]] {
      override def reads(json: JsValue): JsResult[List[T]] = json.validate[JsArray].map(jsArray => jsArray.value.flatMap(jsVal => jsVal.validate[T](tReads).asOpt).toList)
    }
  }

  val fullSeriesReads: Reads[TournamentSeries] = Json.reads[TournamentSeries]

  def using(sortParameter: Option[String]): JsObject =
    SortUtils.by(sortParameter) match {
      case (string, int) => Json.obj(string -> int)
    }


  val seriesPlayerInitReads: Reads[SeriesPlayer] = {
    implicit val rankReads = Json.format[Rank]
    implicit val playerReads = Json.format[Player]
    (
      (JsPath \ "playerId").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
        (JsPath \ "seriesId").read[String] and
        (JsPath \ "player").read[Player](playerReads)
      ) (SeriesPlayer.apply(_, _, _, PlayerScores()))
  }


  object ListWrites {
    def listToJson[W: Writes]: List[W] => JsValue = { ws =>
      Json.toJson(ws.map(Json.toJson(_)))
    }

    implicit class JsonOps(jsonObject: Json.type) {
      def listToJson[W: Writes](ws: List[W]) = ListWrites.listToJson.apply(ws)
    }

  }

  def parseRequestBody[T](request: Request[JsValue])(implicit reads: Reads[T]): Option[T] = {
    request.body.validate[T](reads).asOpt
  }

  implicit val localDateReads = new Reads[LocalDate] {
    override def reads(json: JsValue) = {
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
      json.validate[String].map(LocalDate.parse(_, formatter))
    }
  }


  val tournamentReads: Reads[Tournament] = (
    (JsPath \ "tournamentName").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "tournamentDate").read[LocalDate](localDateReads) and
      (JsPath \ "maximumNumberOfSeriesEntries").read[Int] and
      (JsPath \ "hasMultipleSeries").read[Boolean] and
      (JsPath \ "showClub").read[Boolean]
    ) (Tournament.apply(UUID.randomUUID().toString, _, _, _, _, _))

  val seriesReads: Reads[TournamentSeries] = (
    (JsPath \ "seriesName").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "seriesColor").read[String] and
      (JsPath \ "numberOfSetsToWin").read[Int] and
      (JsPath \ "setTargetScore").read[Int] and
      (JsPath \ "playingWithHandicaps").read[Boolean] and
      (JsPath \ "extraHandicapForRecs").read[Int] and
      (JsPath \ "showReferees").read[Boolean] and
      (JsPath \ "tournamentId").read[String]
    ) (TournamentSeries.apply(UUID.randomUUID().toString, _, _, _, _, _, _, _, 1, _))

  val singleSeriesReads: Reads[TournamentSeries] = (
    (JsPath \ "seriesColor").read[String] and
      (JsPath \ "numberOfSetsToWin").read[Int] and
      (JsPath \ "setTargetScore").read[Int] and
      (JsPath \ "playingWithHandicaps").read[Boolean] and
      (JsPath \ "extraHandicapForRecs").read[Int] and
      (JsPath \ "showReferees").read[Boolean]
    ) (TournamentSeries.apply(UUID.randomUUID().toString, "replacedName", _, _, _, _, _, _, 1, "toBeReplacedId"))


  val tournamentWithSeriesWritesOnlyPlayers: Writes[TournamentWithSeries] = new Writes[TournamentWithSeries] {
    override def writes(o: TournamentWithSeries): JsValue = Json.obj(
      "id" -> o.tournament.id,
      "tournamentName" -> o.tournament.tournamentName,
      "tournamentDate" -> o.tournament.tournamentDate,
      "showClub" -> o.tournament.showClub,
      "hasMultipleSeries" -> o.tournament.hasMultipleSeries,
      "maximumNumberOfSeriesEntries" -> o.tournament.maximumNumberOfSeriesEntries,
      "series" -> o.series.map(Json.toJson(_)(seriesWithPlayersWrites))
    )
  }

  val seriesWithPlayersWrites: Writes[SeriesWithPlayers] = new Writes[SeriesWithPlayers] {
    implicit val rankWrites = Json.writes[Rank]
    implicit val playerWrites = Json.writes[Player]
    implicit val scoreWrites = Json.writes[PlayerScores]
    implicit val seriesPlayerWrites = Json.writes[SeriesPlayer]

    override def writes(o: SeriesWithPlayers): JsValue = Json.obj(
      "id" -> o.seriesId,
      "seriesName" -> o.seriesName,
      "seriesColor" -> o.seriesColor,
      "setTargetScore" -> o.setTargetScore,
      "numberOfSetsToWin" -> o.numberOfSetsToWin,
      "playingWithHandicaps" -> o.playingWithHandicaps,
      "extraHandicapForRecs" -> o.extraHandicapForRecs,
      "showReferees" -> o.showReferees,
      "currentRoundNr" -> o.currentRoundNr,
      "seriesPlayers" -> o.seriesPlayers.map { seriesPlayer => Json.toJson(seriesPlayer.player).asInstanceOf[JsObject] },
      "tournamentId" -> o.tournamentId
    )
  }


  def pingpongMatchReads = {
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
    (
      (JsPath \ "id").read[String] and
        (JsPath \ "playerA").readNullable[Player] and
        (JsPath \ "playerB").readNullable[Player] and
        (JsPath \ "roundId").read[String] and
        (JsPath \ "handicap").read[Int] and
        (JsPath \ "isHandicapForB").read[Boolean] and
        (JsPath \ "targetScore").read[Int] and
        (JsPath \ "numberOfSetsToWin").read[Int] and
        (JsPath \ "wonSetsA").read[Int] and
        (JsPath \ "wonSetsB").read[Int] and
        (JsPath \ "games").lazyRead[List[PingpongGame]](Reads.list[PingpongGame](Json.reads[PingpongGame]))
      ) (PingpongMatch.apply(_, _, _, _, _, _, _, _, _, _, _))
  }

  def optionWrites[T](implicit tWrites: Writes[T]) = new Writes[Option[T]] {
    override def writes(o: Option[T]): JsValue = o.map { t =>
      Json.toJson(t)
    }.getOrElse(JsNull)
  }

}
