package controllers

import javax.inject.Inject

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.HallService
import utils.JsonUtils.ListWrites._
import models.halls.HallEvidence._
import scala.concurrent.ExecutionContext.Implicits.global

class HallController @Inject()(hallService: HallService) extends Controller{

  def getAllHalls() = Action.async{

    hallService.retrieveAll.map{ hallList => Ok(Json.listToJson(hallList))}

  }

}
