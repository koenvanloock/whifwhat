package models

import play.api.libs.functional.syntax.{unlift, _}
import play.api.libs.json.{Reads, Writes, __, _}

import scala.language.postfixOps

object UserFormat {

  val userReads: Reads[User] = (
    (__ \ "_id").read[String] and
      (__ \ "username").read[String] and
      (__ \ "passwordHash").read[String] and
      (__ \ "roleId").read[String]
    )(User.apply(_,_,_,_))

  val userWrites: Writes[User] = (
    (__ \ "_id").write[String] and
      (__ \ "username").write[String] and
      (__ \ "passwordHash").write[String] and
      (__ \ "roleId").write[String]
    )(unlift(User.unapply))

  def writes(o: User): JsObject = Json.format[User].writes(o)

  def reads(json: JsValue): JsResult[User] = Json.format[User].reads(json)

  val responseWrites: Writes[User] = (
    (__ \ "_id").write[String] and
      (__ \ "username").write[String] and
      (__ \ "passwordHash").write[String] and
      (__ \ "roleId").write[String]
    )(unlift(User.unapply))

}
