package models.player

import play.api.libs.functional.syntax._
import play.api.libs.json._
import utils.JsonUtils

import scala.language.postfixOps

object PlayerFormat {

  implicit val rankFormat = Json.format[Rank]

  val playerReads: Reads[Player] = (
    (__ \ "_id").read[String] and
      (__ \ "firstname").read[String] and
      (__ \ "lastname").read[String] and
      (__ \ "rank").read[Rank] and
      (__ \ "imagePath").readNullable[String]
  )(Player.apply(_,_,_,_,_))

  val playerWrites: Writes[Player] = (
    (__ \ "_id").write[String] and
      (__ \ "firstname").write[String] and
      (__ \ "lastname").write[String] and
      (__ \ "rank").write[Rank] and
      (__ \ "imagePath").writeNullable[String]
    )(unlift(Player.unapply))


  val responseWrites: Writes[Player] = (
    (__ \ "id").write[String] and
      (__ \ "firstname").write[String] and
      (__ \ "lastname").write[String] and
      (__ \ "rank").write[Rank] and
      (__ \ "imagePath").writeNullable[String]
    )(unlift(Player.unapply))

  val requestReads: Reads[Player] = (
    (__ \ "id").read[String] and
      (__ \ "firstname").read[String] and
      (__ \ "lastname").read[String] and
      (__ \ "rank").read[Rank] and
      (__ \ "imagePath").readNullable[String]
    )(Player.apply(_,_,_,_,_))

  def reads(json: JsValue): JsResult[Player] = Json.fromJson(json)(playerReads)

  def writes(player: Player): JsObject = Json.toJson(player)(playerWrites).asInstanceOf[JsObject]
}
