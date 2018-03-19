package models

import play.api.libs.json.OFormat

trait Evidence[M] extends OFormat[M] {
  def getId(m: M): Option[String]

  def setId(id: String)(m: M): M
}