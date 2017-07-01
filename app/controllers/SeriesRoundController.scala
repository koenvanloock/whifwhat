package controllers

import java.util.UUID
import javax.inject.Inject

import models._
import play.api.libs.json.Reads._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller, Result}
import services.{SeriesRoundService, SeriesService}
import utils.{ControllerUtils, JsonUtils, RoundRanker}
import utils.JsonUtils.ListWrites._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import SeriesRoundEvidence._
import models.matches.MatchEvidence.matchIsModel._
import models.matches.{MatchEvidence, PingpongGame, PingpongMatch}
import models.player.{Player, PlayerScores, Rank, SeriesPlayer}

class SeriesRoundController @Inject()(seriesRoundService: SeriesRoundService, seriesService: SeriesService) extends Controller{
  type FinalRanking = List[SeriesPlayer]

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


  implicit val rankWrites = Json.writes[Rank]
  implicit val scoresWrites = Json.writes[PlayerScores]
  implicit val playerWrites = Json.writes[Player]
  implicit val seriesPlayerWrites = Json.writes[SeriesPlayer]

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
    ControllerUtils.parseEntityFromRequestBody(request, Json.reads[PingpongMatch]).map{ siteMatch =>
       val matchWithSetResults = calculateSets(siteMatch)
        seriesRoundService.updateRoundWithMatch(matchWithSetResults, seriesRoundId).map(showRetrieveRoundResult)
    }.getOrElse(Future(BadRequest))
  }

  def calculateSets(siteMatch: PingpongMatch): PingpongMatch = {
    val setTuple = siteMatch.games.foldRight((0,0))( (game, tuple) => if(game.isCorrect(siteMatch.targetScore) && game.pointA > game.pointB) (tuple._1 +1, tuple._2) else if(game.isCorrect(siteMatch.targetScore) && game.pointA < game.pointB) (tuple._1, tuple._2 +1) else tuple )
    siteMatch.copy(wonSetsA = setTuple._1, wonSetsB = setTuple._2)
  }

  def getRound(roundId: String) = Action.async{
    seriesRoundService
      .getRound(roundId)
      .map(showRetrieveRoundResult)
  }

  def getMatchListOfRound(seriesRoundId: String) = Action.async{
    implicit val rankWrites = Json.writes[Rank]
    implicit val playerWrites = Json.writes[Player]
    implicit val gameWrites = Json.writes[PingpongGame]
    implicit val matchWrites = Json.writes[PingpongMatch]
    seriesRoundService.getMatchesOfRound(seriesRoundId).map( matchList => Ok(Json.listToJson(matchList)))
  }

  def getNextRoundOrWinnerView(seriesRoundId: String, scoreType: String) = Action.async  {
    val isWithHandicap = true //todo fech series
    val parsedScoreType = ScoreTypes.parseScoreType(scoreType)
    seriesRoundService.getRound(seriesRoundId).flatMap{
      case Some(seriesRound) => if(seriesRound.isComplete) {
        val rankedPlayers = RoundRanker.calculateRoundResults(seriesRound, isWithHandicap, parsedScoreType)
        // todo add these results to seriesPlayer if needed and draw and return the next series
        Future(Ok)
      } else {
        Future(Ok)
      }
      case None => Future(BadRequest)
    }
  }

  def proceedToNextRound(seriesId: String, roundNr: Int) = Action.async {
    seriesService.retrieveById(seriesId).flatMap {
      case Some(series) => seriesRoundService.retrieveByFields(Json.obj("roundNr" -> series.currentRoundNr, "seriesId" -> seriesId))
        .flatMap(showNextRoundOrFinalRanking(series, roundNr))
      case None => Future(BadRequest("Reeks niet gevonden"))
    }
  }

  //todo move logic to service layer ???
  def checkCompleteAndAdvanceIfPossible(series: TournamentSeries, seriesRound: SeriesRound): Future[Either[String, Either[FinalRanking, SeriesRound]]] = {
    if (seriesRound.isComplete) {
      val roundRanking = RoundRanker.calculateRoundResults(seriesRound, series.playingWithHandicaps, ScoreTypes.DIRECT_CONFRONTATION)
      seriesService.advanceIfPossibleOrShowFinalRanking(series, roundRanking).map(Right(_))
    } else {
      Future(Left("The current round isn't complete"))
    }
  }

  def showNextRoundOrFinalRanking(series: TournamentSeries, roundNr: Int): Option[SeriesRound] => Future[Result] = {
    case Some(seriesRound) => if(roundNr > series.currentRoundNr){
                                checkCompleteAndAdvanceIfPossible(series, seriesRound).map(showProceedResult)
                              } else {
                                seriesRoundService.retrieveByFields(Json.obj("roundNr" -> series.currentRoundNr, "seriesId" -> series.id)).map {
                                  case Some(retrievedRound) => Ok(Json.toJson(retrievedRound))
                                  case _ => BadRequest("Round not found")
                                }
                              }
    case _ => Future(BadRequest("Geen ronde gevonden"))
  }

  def showProceedResult: (Either[String, Either[FinalRanking, SeriesRound]]) => Result = {
    case Right(completedResult) => completedResult match {
      case Right(roundToShow) => Ok(Json.toJson(roundToShow))
      case Left(playerOrder) => Ok(Json.listToJson(playerOrder))
    }
    case Left(error) => BadRequest(error)
  }

  def showPreviousRound(seriesId: String, roundNr: Int) = Action.async{
    seriesRoundService.retrieveByFields(Json.obj("roundNr" -> roundNr, "seriesId" -> seriesId)).map(showRetrieveRoundResult)
  }

  def isLastRoundOfSeries(seriesId: String) = Action.async{
    seriesService.retrieveById(seriesId).flatMap{
      case Some(series) =>
          seriesRoundService.retrieveAllByField("seriesId", seriesId).map { list =>
            val isLast = list.length == series.currentRoundNr
            Ok(Json.obj("lastRound" -> isLast))
          }
      case None => Future(Ok(Json.obj("lastRound" -> false)))
    }
  }

  def getActiveRoundOfSeries(seriesId: String) = Action.async{
    seriesService.retrieveById(seriesId).flatMap{
      case Some(series) => seriesRoundService
                              .retrieveByFields(Json.obj("seriesId" -> seriesId, "roundNr" -> series.currentRoundNr))
                              .map(showRetrieveRoundResult)
      case _ => Future(BadRequest("De gevraagde reeks kon niet gevonden worden."))
    }
  }

  private def showRetrieveRoundResult: Option[SeriesRound] => Result = {
    case Some(round) => Ok(Json.toJson(round))
    case _ => BadRequest("De gevraagde ronde kon niet gevonden worden.")
  }
}
