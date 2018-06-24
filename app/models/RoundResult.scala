package models

import models.player.{Player, PlayerScores, Rank, SeriesPlayer}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

case class RoundResult(id: String, seriesRoundId: String, results: List[ResultLine], robinResult: Option[List[List[ResultLine]]]) extends repositories.numongo.Model[String]

case class ResultLine(player: SeriesPlayer, tieScore: Option[PlayerScores])

object RoundResultEvidence{
  implicit val playerScoreFormat = Json.format[PlayerScores]
  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  implicit val seriesPlayerFormat = Json.format[SeriesPlayer]
  implicit val resultLineFormat = Json.format[ResultLine]

  implicit val resultLineListReads = new Reads[List[ResultLine]]{
    override def reads(json: JsValue): JsResult[List[ResultLine]] = Json.fromJson[List[ResultLine]](json)
  }

  implicit object roundResultEvidence extends Model[RoundResult]{
    override def getId(m: RoundResult): Option[String] = Some(m.id)

    override def setId(id: String)(m: RoundResult): RoundResult = m.copy(id=id)

    override def writes(o: RoundResult): JsObject = Json.toJson(o)(roundResultWrites).asInstanceOf[JsObject]

    override def reads(json: JsValue): JsResult[RoundResult] = Json.fromJson(json)(roundResultReads)

    implicit val resultLineWrites: Writes[ResultLine] = (
      (__ \ "player").write[SeriesPlayer] and
        (__ \ "tieScore").writeNullable[PlayerScores]
      ) (unlift(ResultLine.unapply))

    implicit val roundResultWrites: Writes[RoundResult] = (
      (__ \ "id").write[String] and
      (__ \ "seriesRoundId").write[String] and
        (__ \ "resultLines").lazyWrite[List[ResultLine]](Writes.list[ResultLine](resultLineWrites)) and
        (__ \ "robinResult").writeNullable[List[List[ResultLine]]]
      ) (unlift(RoundResult.unapply))

    implicit val resultLineReads: Reads[ResultLine] = (
      (__ \ "player").read[SeriesPlayer] and
        (__ \ "tieScore").readNullable[PlayerScores]
      ) (ResultLine.apply(_,_))

    implicit val roundResultReads: Reads[RoundResult] = (
      (__ \ "id").read[String] and
      (__ \ "seriesRoundId").read[String] and
        (__ \ "resultLines").lazyRead[List[ResultLine]](Reads.list[ResultLine](resultLineReads)) and
          (__ \ "robinResult").readNullable[List[List[ResultLine]]](Reads.list[List[ResultLine]](resultLineListReads))
      ) (RoundResult.apply(_,_,_,_))


  }
}