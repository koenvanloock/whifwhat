package models

import play.api.libs.json.{JsObject, JsResult, JsValue, Json}
import slick.jdbc.GetResult

case class Role(id: String, roleName: String)

  object RoleEvidence {

    implicit object roleIsModel extends Model[Role] {
      override def getId(m: Role): Option[String] = Some(m.id)

      override def setId(id: String)(m: Role): Role = m.copy(id = id)

      override def writes(o: Role): JsObject = Json.format[Role].writes(o)

      override def reads(json: JsValue): JsResult[Role] = Json.format[Role].reads(json)
    }

  }

case class User(id: String, username: String, paswordHash: String, roleId: String)

  object UserEvidence {

    implicit object userIsModel extends Model[User] {
      override def getId(user: User): Some[String] = Some(user.id)

      override def setId(newId: String)(user: User) = user.copy(id = newId)

      override def writes(o: User): JsObject = Json.format[User].writes(o)

      override def reads(json: JsValue): JsResult[User] = Json.format[User].reads(json)
    }

  }

object RoleResultModel{
  implicit object RoleIsCrudable extends Crudable[Role]{
    override def getId(crudable: Role): String = crudable.id

    override implicit val getResult: GetResult[Role] = GetResult(r => Role(r.<<,r.<<))
  }
}

object UserResultModel{
  implicit object RoleIsCrudable extends Crudable[User]{
    override def getId(crudable: User): String = crudable.roleId

    override implicit val getResult: GetResult[User] = GetResult(r => User(r.<<,r.<<,r.<<,r.<<))
  }
}