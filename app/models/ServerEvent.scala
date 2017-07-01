package models

import play.api.libs.json.JsObject

case class ServerEvent(event: String, jsObject: JsObject)
