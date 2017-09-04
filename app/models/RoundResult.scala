package models

import models.player.{Player, PlayerScores, Rank}
import play.api.libs.functional.syntax._
import play.api.libs.json
import play.api.libs.json.Reads._
import play.api.libs.json._

case class RoundResult(id: String, seriesRoundId: String, results: List[ResultLine]) {}

case class ResultLine(player: Player, tieScore: Option[PlayerScores])

object RoundResultEvidence{
  implicit val playerScoreFormat = Json.format[PlayerScores]
  implicit val rankFormat = Json.format[Rank]
  implicit val Player = Json.format[Player]

  implicit object roundResultEvidence extends Model[RoundResult]{
    override def getId(m: RoundResult): Option[String] = Some(m.id)

    override def setId(id: String)(m: RoundResult): RoundResult = m.copy(id=id)

    override def writes(o: RoundResult): JsObject = Json.toJson(roundResultWrites).asInstanceOf[JsObject]

    override def reads(json: JsValue): JsResult[RoundResult] = Json.fromJson(json)(roundResultReads)

    implicit val resultLineWrites: Writes[ResultLine] = (
      (__ \ "player").write[Player] and
        (__ \ "tieScore").writeNullable[PlayerScores]
      ) (unlift(ResultLine.unapply))

    implicit val roundResultWrites: Writes[RoundResult] = (
      (__ \ "id").write[String] and
      (__ \ "seriesRoundId").write[String] and
        (__ \ "resultLines").lazyWrite[List[ResultLine]](Writes.list[ResultLine](resultLineWrites))
      ) (unlift(RoundResult.unapply))

    implicit val resultLineReads: Reads[ResultLine] = (
      (__ \ "player").read[Player] and
        (__ \ "tieScore").readNullable[PlayerScores]
      ) (ResultLine.apply(_,_))

    implicit val roundResultReads: Reads[RoundResult] = (
      (__ \ "id").read[String] and
      (__ \ "seriesRoundId").read[String] and
        (__ \ "resultLines").lazyRead[List[ResultLine]](Reads.list[ResultLine](resultLineReads))
      ) (RoundResult.apply(_,_,_))


  }
}