package utils


import java.time.LocalDate
import java.util.UUID

import models.Tournament
import play.api.Logger
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.mvc.{Request, AnyContent}
import play.api.libs.json.Reads._ // Custom validation helpers
import play.api.libs.functional.syntax._ // Combinator syntax


object JsonUtils {

  object ListWrites {
    def listToJson[W: Writes]: List[W] => JsValue = { ws =>
      Json.toJson(ws.map(Json.toJson(_)))
    }

    implicit class JsonOps(jsonObject: Json.type) {
      def listToJson[W: Writes](ws: List[W]) = ListWrites.listToJson.apply(ws)
    }

  }

  def parseRequestBody[T](request: Request[AnyContent])(implicit reads: Reads[T]): Option[T] = {
      request.body.asJson.flatMap( json => {
        println(json.toString())
        json.validate[T](reads).asOpt})
  }

  implicit val localDateReads: Reads[LocalDate] = (
      (JsPath \ "year").read[Int] and
      (JsPath \ "month").read[Int] and
    (JsPath \ "day").read[Int]
    )(LocalDate.of(_,_,_))

  val tournamentReads: Reads[Tournament] = (
    (JsPath \ "tournamentName").read[String](minLength[String](1) keepAnd maxLength[String](255)) and
      (JsPath \ "tournamentDate").read[LocalDate](localDateReads) and
      (JsPath \ "maximumNumberOfSeriesEntries").read[Int] and
      (JsPath \ "hasMultipleSeries").read[Boolean] and
      (JsPath \ "showClub").read[Boolean]
    ) (Tournament.apply(UUID.randomUUID().toString,_, _, _, _, _))

}
