package repositories.numongo

import play.api.libs.json.{JsNumber, JsObject, JsString, JsValue}
import play.api.libs.json.Json.JsValueWrapper
import reactivemongo.bson.BSONObjectID

trait Model[IdType] {
    def id: IdType
}