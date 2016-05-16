package utils


import play.api.Logger
import play.api.libs.json._
import play.api.mvc.{Request, AnyContent}


object JsonUtils {

  def parseRequestBody[T](request: Request[AnyContent])(implicit reads: Reads[T]): Option[T] = {
      request.body.asJson.flatMap( json => {
        println(json.toString())
        json.validate[T](reads).asOpt})
  }

}
