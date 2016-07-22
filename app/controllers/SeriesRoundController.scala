package controllers

import java.util.UUID
import javax.inject.Inject

import models._
import models.matches.{SiteGame, SiteMatchWithGames}
import models.player.{Rank, PlayerScores, SeriesRoundPlayer, SeriesPlayerWithScores}
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.functional.syntax._ // Combinator syntax
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

  val robinInitReads: Reads[SiteRobinRound] = (
    (JsPath \ "numberOfRobinGroups").read[Int] and
      (JsPath \ "seriesId").read[String] and
      (JsPath \ "roundNr").read[Int]
    ) (SiteRobinRound.apply(UUID.randomUUID().toString,_, _, _))

  val bracketInitReads: Reads[SiteBracketRound] = (
    (JsPath \ "numberOfBracketRounds").read[Int] and
      (JsPath \ "seriesId").read[String] and
      (JsPath \ "roundNr").read[Int]
    ) (SiteBracketRound.apply(UUID.randomUUID().toString,_, _, _))

  val roundReadsInsert = new Reads[SeriesRound]{

    def robinreads(json: JsValue) = Json.fromJson[SiteRobinRound](json)(robinInitReads)
    def bracketreads(json: JsValue) = Json.fromJson[SiteBracketRound](json)(bracketInitReads)

    override def reads(json: JsValue): JsResult[SeriesRound] = (json \ "roundType").as[String] match {
      case "R" => robinreads(json)
      case "B" => bracketreads(json)
    }
  }

  val fullRoundReads = new Reads[SeriesRoundWithPlayersAndMatches]{
    override def reads(json: JsValue): JsResult[SeriesRoundWithPlayersAndMatches] = (json \ "robins").asOpt[JsValue].map{ robinJson =>
      JsSuccess(RobinRound(fullRobinReads(robinJson)))
    }.getOrElse(JsSuccess(fullBracketReads(json)))
  }

  def fullRobinReads(json: JsValue): List[RobinGroup] = {
    implicit val rankReads =  Json.reads[Rank]
    implicit val  scoreReads = Json.reads[PlayerScores]
    implicit val seriesPlayerListReads = new Reads[List[SeriesRoundPlayer]]{
      override def reads(json: JsValue): JsResult[List[SeriesRoundPlayer]] = JsSuccess(json.as[JsArray].value.flatMap( entity => Json.fromJson[SeriesRoundPlayer](entity)(Json.reads[SeriesRoundPlayer]).asOpt).toList)
    }
    implicit val siteGameListReads = new Reads[List[SiteGame]]{
      override def reads(json: JsValue): JsResult[List[SiteGame]] = JsSuccess(json.as[JsArray].value.flatMap( entity => Json.fromJson[SiteGame](entity)(Json.reads[SiteGame]).asOpt).toList)
    }

    implicit val siteMatchListReads = new Reads[List[SiteMatchWithGames]]{
      override def reads(json: JsValue): JsResult[List[SiteMatchWithGames]] = JsSuccess(json.as[JsArray].value.flatMap( entity => Json.fromJson[SiteMatchWithGames](entity)(Json.reads[SiteMatchWithGames]).asOpt).toList)
    }

    val robinGroupReads: Reads[RobinGroup] = (
        (JsPath \ "robinGroupId").read[String] and
          (JsPath \ "robinPlayers").read[List[SeriesRoundPlayer]] and
          (JsPath \ "robinMatches").read[List[SiteMatchWithGames]]
        )(RobinGroup.apply(_,_,_))

    json.as[JsArray].value.flatMap(entry => Json.fromJson(entry)(robinGroupReads).asOpt).toList
  }

  def fullBracketReads(json: JsValue): Bracket = {
    Bracket("TESTROUND",List(),List())
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

  def createSeriesRound() = Action.async{ request =>
    ControllerUtils.parseEntityFromRequestBody(request, roundReadsInsert).map{ seriesRound =>
      seriesRoundService.createSeriesRound(seriesRound).map{
        case Some(round) => Created(Json.toJson(round)(Json.writes[GenericSeriesRound]))
        case _ => InternalServerError
      }
    }.getOrElse(Future(BadRequest("ongeldig formaat")))
  }

  def saveRound = Action.async{ request =>
    ControllerUtils.parseEntityFromRequestBody(request, fullRoundReads).map { fullSeriesRound =>

      seriesRoundService.saveFullSeriesRound(fullSeriesRound).map(_ => Ok)
    }.getOrElse(Future(BadRequest("foute invoer")))
  }
}
