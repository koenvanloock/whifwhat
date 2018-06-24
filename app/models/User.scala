package models

import play.api.libs.json.{JsObject, JsResult, JsValue, Json}

case class Role(id: String, roleName: String) extends repositories.numongo.Model[String]

  object RoleEvidence {

    implicit object roleIsModel extends Model[Role] {
      override def getId(m: Role): Option[String] = Some(m.id)

      override def setId(id: String)(m: Role): Role = m.copy(id = id)

      override def writes(o: Role): JsObject = Json.format[Role].writes(o)

      override def reads(json: JsValue): JsResult[Role] = Json.format[Role].reads(json)
    }

  }

case class User(id: String, username: String, passwordHash: String, roleId: String) extends repositories.numongo.Model[String]

  object UserEvidence {

    implicit object userIsModel extends Model[User] {
      override def getId(user: User): Some[String] = Some(user.id)

      override def setId(newId: String)(user: User) = user.copy(id = newId)

      override def writes(o: User): JsObject = Json.format[User].writes(o)

      override def reads(json: JsValue): JsResult[User] = Json.format[User].reads(json)
    }

  }