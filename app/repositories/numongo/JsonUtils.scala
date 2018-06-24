package repositories.numongo

import play.api.libs.json._

object JsonUtils {
  def listWrites[T](implicit tWrites: Writes[T]) = {
    new Writes[List[T]] {
      override def writes(list: List[T]): JsValue = Json.toJson(list.map(Json.toJson(_)(tWrites)))
    }
  }

  def listReads[T](implicit tReads: Reads[T]) = {
    new Reads[List[T]] {
      override def reads(json: JsValue): JsResult[List[T]] = json.validate[JsArray].map(jsArray => jsArray.value.flatMap(jsVal => jsVal.validate[T](tReads).asOpt).toList)
    }
  }


  def using(sortParameter: String): JsObject = {
    val (int, string) = SortUtils.by(sortParameter)
    Json.obj(string -> int)
  }


  object ListWrites {
    def listToJson[W: Writes]: List[W] => JsValue = { ws =>
      Json.toJson(ws.map(Json.toJson(_)))
    }

    implicit class JsonOps(jsonObject: Json.type) {
      def listToJson[W: Writes](ws: List[W]) = ListWrites.listToJson.apply(ws)
    }

  }

}