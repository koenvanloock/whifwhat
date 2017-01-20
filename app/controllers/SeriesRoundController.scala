package controllers

import java.util.UUID
import javax.inject.Inject

import models._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}
import services.SeriesRoundService
import utils.ControllerUtils
import utils.JsonUtils.ListWrites._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import SeriesRoundEvidence._
import models.matches.MatchEvidence.matchIsModel._
import models.matches.{MatchEvidence, SiteMatch}

class SeriesRoundController @Inject()(seriesRoundService: SeriesRoundService) extends Controller{

  implicit val robinInitReads: Reads[SiteRobinRound] = (
    (JsPath \ "numberOfRobinGroups").read[Int] and
      (JsPath \ "seriesId").read[String] and
      (JsPath \ "roundNr").read[Int]
    ) (SiteRobinRound.apply(UUID.randomUUID().toString,_, _, _, Nil))

  implicit val bracketInitReads: Reads[SiteBracketRound] = (
    (JsPath \ "numberOfBracketRounds").read[Int] and
      (JsPath \ "seriesId").read[String] and
      (JsPath \ "roundNr").read[Int]
    ) (SiteBracketRound.apply(UUID.randomUUID().toString,_, _, _, Nil, SiteBracket.buildBracket(1,21,2)))

  val robinconfigReads: Reads[SiteRobinRound] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "numberOfRobinGroups").read[Int] and
      (JsPath \ "seriesId").read[String] and
      (JsPath \ "roundNr").read[Int]
    ) (SiteRobinRound.apply(_,_, _, _, Nil))

  val bracketConfigReads: Reads[SiteBracketRound] = (
    (JsPath \ "id").read[String] and
      (JsPath \ "numberOfBracketRounds").read[Int] and
      (JsPath \ "seriesId").read[String] and
      (JsPath \ "roundNr").read[Int]
    ) (SiteBracketRound.apply(_,_, _, _, Nil, SiteBracket.buildBracket(1,21,2)))

  val roundReadsInsert = new Reads[SeriesRound]{


    override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match {
      case "R" => Json.fromJson[SiteRobinRound](json)(robinInitReads)
      case "B" => Json.fromJson[SiteBracketRound](json)(bracketInitReads)
    }
  }

  val roundReadsConfig = new Reads[SeriesRound]{
    override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match {
      case "R" => Json.fromJson[SiteRobinRound](json)(robinconfigReads)
      case "B" => Json.fromJson[SiteBracketRound](json)(bracketConfigReads)
    }
  }

  def getRoundsOfSeries(seriesId: String) = Action.async{

    seriesRoundService.getRoundsOfSeries(seriesId).map(roundList => Ok(Json.listToJson(roundList.sortBy(_.roundNr))))

  }

  def updateConfigSeriesRound() = Action.async{ request =>
    ControllerUtils.parseEntityFromRequestBody(request, roundReadsConfig).map{ seriesRound =>
      seriesRoundService.updateSeriesRound(seriesRound).map {
        updatedSeriesRound => Ok(Json.toJson(updatedSeriesRound))
      }
    }.getOrElse(Future(BadRequest))
  }

  def fullUpdateSeriesRound = Action.async{ request =>
    ControllerUtils.parseEntityFromRequestBody(request, SeriesRoundEvidence.seriesRoundIsModel.roundReads).map{ seriesRound =>
      seriesRoundService.updateSeriesRound(seriesRound).map {
        updatedSeriesRound => Ok(Json.toJson(updatedSeriesRound))
      }
    }.getOrElse(Future(BadRequest))
  }

  def createSeriesRound(seriesId: String) = Action.async{
      val seriesRound =  SiteBracketRound(UUID.randomUUID().toString,0, seriesId,0,Nil, SiteBracket.buildBracket(1,21,2))
      seriesRoundService.createSeriesRound(seriesRound).map{
        round => Created(Json.toJson(round))
      }
  }

  def deleteSeriesRound(seriesRoundId: String) = Action.async(seriesRoundService.delete(seriesRoundId).map{_ => NoContent})

  def updateRoundMatch(seriesRoundId: String) = Action.async{ request =>
    ControllerUtils.parseEntityFromRequestBody(request, Json.reads[SiteMatch]).map{ siteMatch =>
        seriesRoundService.updateRoundWithMatch(siteMatch, seriesRoundId).map{
          case Some(updatedRound) => Ok(Json.toJson(updatedRound))
          case _ => InternalServerError
        }
    }.getOrElse(Future(BadRequest))
  }
}
