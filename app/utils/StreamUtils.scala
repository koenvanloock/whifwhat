package utils

import play.api.libs.iteratee.Concurrent
import play.api.libs.json.{JsObject, Json, Writes}

object StreamUtils {

  def pushJsonObjectToStream[A](objectDesc: String, objectToPush: A, channel: Concurrent.Channel[JsObject])(implicit aWrites: Writes[A]): Unit =
  {
    println(s"printing $objectDesc to scorestream: " + objectToPush)
    channel.push(Json.obj(objectDesc -> Json.toJson(objectToPush)(aWrites)))
  }


  def pushJsonObjectToStream(objectDesc: String, jsonObj: JsObject, channel: Concurrent.Channel[JsObject]): Unit =
  {
    println(s"printing $objectDesc to scorestream: " + jsonObj)
    channel.push(Json.obj(objectDesc -> jsonObj))
  }
}
