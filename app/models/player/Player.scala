package models.player

import models.Model
import play.api.libs.json.{JsObject, JsResult, JsValue, Json}

case class Player(id: String, firstname: String, lastname: String, rank: Rank, imagepath: Option[String]=None)

object PlayerEvidence{
  implicit object PlayerIsModel extends Model[Player]{
    implicit val rankFormat = Json.format[Rank]
    override def getId(m: Player): Option[String] = Some(m.id)

    override def setId(id: String)(m: Player): Player = m.copy(id = id)

    override def writes(o: Player): JsObject = Json.format[Player].writes(o)

    override def reads(json: JsValue): JsResult[Player] = Json.format[Player].reads(json)
  }
}
