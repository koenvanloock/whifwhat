package utils

import play.api.libs.json.Reads
import play.api.mvc.{AnyContent, Request}

object ControllerUtils {


  def parseEntityFromRequestBody[T](request: Request[AnyContent], entityReads: Reads[T]): Option[T] = {
    request.body.asJson.flatMap{ json => println(json);json.validate[T](entityReads).asOpt}
  }
}
