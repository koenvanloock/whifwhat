package models.halls

import models.Model
import models.player.{Player, Rank}
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}
import utils.JsonUtils

case class Hall(id: String, hallName: String, numberOfRows: Int, numberOfTablesPerRow: Int, tables: List[HallTable])


object HallEvidence{
  implicit object HallIsModel extends Model[Hall]{
    implicit val rankFormat = Json.format[Rank]
    implicit val playerFormat = Json.format[Player]
    val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)

    implicit val hallTableFormat = Json.format[HallTable]

    override def getId(m: Hall): Option[String] = Some(m.id)

    override def setId(id: String)(m: Hall): Hall = m.copy(id = id)

    override def writes(o: Hall): JsObject = Json.format[Hall].writes(o)

    override def reads(json: JsValue): JsResult[Hall] = Json.format[Hall].reads(json)
  }


}