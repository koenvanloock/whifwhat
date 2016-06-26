package controllers

import javax.inject.Inject

import models.{SiteBracketRound, SiteRobinRound, SeriesRound, GenericSeriesRound}
import play.api.libs.json.{JsResult, JsValue, Reads, Json}
import play.api.mvc.{Action, Controller}
import services.SeriesRoundService
import utils.ControllerUtils
import utils.JsonUtils.ListWrites._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SeriesRoundController @Inject()(seriesRoundService: SeriesRoundService) extends Controller{

  val roundReads = new Reads[SeriesRound]{

    def robinreads(json: JsValue) = Json.fromJson[SiteRobinRound](json)(Json.reads[SiteRobinRound])
    def bracketreads(json: JsValue) = Json.fromJson[SiteBracketRound](json)(Json.reads[SiteBracketRound])

    override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match {
      case "R" => robinreads(json)
      case "B" => bracketreads(json)
    }
  }

  def getRoundsOfSeries(seriesId: String) = Action.async{

    seriesRoundService.getRoundsOfSeries(seriesId).map(roundList => Ok(Json.listToJson(roundList.sortBy(_.roundNr))(Json.format[GenericSeriesRound])))

  }

  def updateSeriesRound() = Action.async{ request =>
    ControllerUtils.parseEntityFromRequestBody(request, roundReads).map{ seriesRound =>
      seriesRoundService.updateSeriesRound(seriesRound).map {
        case Some(updatedSeriesRound) => Ok(Json.toJson(updatedSeriesRound)(Json.writes[GenericSeriesRound]))
        case _ => BadRequest("update mislukt")
      }
    }.getOrElse(Future(BadRequest))



  }
}
