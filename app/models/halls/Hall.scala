package models.halls

import models.{Evidence, Model}
import models.matches.MatchEvidence._
import models.matches.PingpongMatch
import models.player.{Player, Rank}
import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.JsonUtils

import scala.language.postfixOps


case class Hall(id: String, hallName: String, rows: Int, tablesPerRow: Int, tables: List[HallTable])


object HallEvidence extends Evidence[Hall] {
    implicit val rankFormat = Json.format[Rank]
    implicit val playerFormat = Json.format[Player]
    val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)


    implicit val hallTableWrites: Writes[HallTable] = (
      (__ \ "hallId").write[String] and
      (__ \ "row").write[Int] and
        (__ \ "column").write[Int] and
        (__ \ "match").writeNullable[PingpongMatch] and
        (__ \ "referee").writeNullable[Player] and
        (__ \ "horizontal").write[Boolean] and
        (__ \ "hidden").write[Boolean] and
        (__ \ "isGreen").write[Boolean] and
        (__ \ "tableName").write[String]
    ) (unlift(HallTable.unapply))

    implicit val hallTableReads: Reads[HallTable] = (
      (__ \ "hallId").read[String] and
      (__ \ "row").read[Int] and
        (__ \ "column").read[Int] and
        (__ \ "match").readNullable[PingpongMatch] and
        (__ \ "referee").readNullable[Player] and
        (__ \ "horizontal").read[Boolean] and
        (__ \ "hidden").read[Boolean] and
        (__ \ "isGreen").read[Boolean] and
        (__ \ "tableName").read[String]
      ) (HallTable.apply(_,_, _, _, _, _, _,_, _))


    implicit val hallTableFormat = Json.format[HallTable]

    override def getId(m: Hall): Option[String] = Some(m.id)

    override def setId(id: String)(m: Hall): Hall = m.copy(id = id)

    override def writes(o: Hall): JsObject = Json.format[Hall].writes(o)

    override def reads(json: JsValue): JsResult[Hall] = Json.format[Hall].reads(json)

}