package repositories.numongo

import play.api.libs.json._

object PageWrites {
  implicit def searchResultsWrites[T](implicit fmt: Writes[T]): Writes[Page[T]] = (ts: Page[T]) => JsObject(Seq(
    "page" -> JsNumber(ts.pageNumber),
    "pageSize" -> JsNumber(ts.elementsPerPage),
    "total" -> JsNumber(ts.totalSize),
    "data" -> JsArray(ts.data.map(Json.toJson(_)(fmt)))
  ))
}