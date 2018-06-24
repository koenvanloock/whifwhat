package models

import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json._

import scala.language.postfixOps

object RoleFormat {
  val roleReads: Reads[Role] = (
    (__ \ "_id").read[String] and
      (__ \ "roleName").read[String]
    )(Role.apply(_,_))

  val userWrites: Writes[Role] = (
    (__ \ "_id").write[String] and
      (__ \ "roleName").write[String]
    )(unlift(Role.unapply))

  def writes(o: Role): JsObject = Json.toJson(o)(userWrites).asInstanceOf[JsObject]

  def reads(json: JsValue): JsResult[Role] = roleReads.reads(json)
}
