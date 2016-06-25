package controllers

import javax.inject.Inject

import models.GenericSeriesRound
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.SeriesRoundService
import utils.JsonUtils.ListWrites._
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesRoundController @Inject()(seriesRoundService: SeriesRoundService) extends Controller{


  def getRoundsOfSeries(seriesId: String) = Action.async{

    seriesRoundService.getRoundsOfSeries(seriesId).map(roundList => Ok(Json.listToJson(roundList)(Json.format[GenericSeriesRound])))

  }
}
