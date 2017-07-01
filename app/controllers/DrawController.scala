package controllers

import javax.inject.Inject

import models.player._
import models._
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, Controller}
import services.SeriesService
import utils.JsonUtils
import models.SeriesRoundEvidence._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DrawController @Inject()(seriesService: SeriesService) extends Controller{

  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)

  def drawSeriesOfTournament(tournamentId: String) = Action.async{

    seriesService.drawTournamentFirstRounds(tournamentId).map{ drawnRoundsResults =>
      val result = drawnRoundsResults.map{
        case Left(error) => Json.toJson(error)(Json.writes[DrawError])
        case Right(seriesRoundWithPlayersAndMatches) => Json.obj("seriesId" -> seriesRoundWithPlayersAndMatches._1,
                                                                  "round"   -> Json.toJson(seriesRoundWithPlayersAndMatches._2))

      }

      Ok(Json.toJson(result))
    }
  }

  def redraw = Action.async { request =>

    JsonUtils.parseRequestBody(request)(seriesRoundIsModel.roundReads).map { round =>
      val resultFut: Future[JsValue] = seriesService.drawRound(round).map {

        case Left(error) => Json.toJson(error)(Json.writes[DrawError])
        case Right(seriesRoundWithPlayersAndMatches) =>
          Json.obj(
            "seriesId" -> seriesRoundWithPlayersAndMatches._1,
            "round" -> Json.toJson(seriesRoundWithPlayersAndMatches._2)
          )
      }

      resultFut.map( result => Ok(Json.toJson(result)))
    }.getOrElse(Future(BadRequest("Dataformaat incorrect")))
  }
}
