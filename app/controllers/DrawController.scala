package controllers

import javax.inject.Inject

import models.matches.{BracketMatch, SiteGame, SiteMatch, SiteMatchWithGames}
import models.player._
import models._
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Action, Controller}
import services.{DrawService, SeriesService}
import utils.JsonUtils
import utils.JsonUtils.ListWrites._

import scala.concurrent.ExecutionContext.Implicits.global

class DrawController @Inject()(seriesService: SeriesService) extends Controller{

  implicit val rankFormat = Json.format[Rank]
  implicit val playerFormat = Json.format[Player]
  val optionPlayerWrites = JsonUtils.optionWrites(playerFormat)

    val siteMatchWrites = new Writes[SiteMatch] {
      override def writes(siteMatch: SiteMatch) = Json.obj(
        "id" -> siteMatch.id,
        "playerA" -> Json.toJson(siteMatch.playerA),
        "playerB" -> Json.toJson(siteMatch.playerB),
        "handicap"-> siteMatch.handicap,
        "isHandicapForB"-> siteMatch.isHandicapForB,
        "targetScore" -> siteMatch.targetScore,
        "numberOfSetsToWin" -> siteMatch.numberOfSetsToWin,
        "games" -> Json.listToJson(siteMatch.games)(Json.format[SiteGame])
      )
    }

    val bracketMatchWrites = new Writes[BracketMatch] {
      override def writes(bracketMatchWithGames: BracketMatch): JsValue = Json.obj(
        "bracketMatchId" -> bracketMatchWithGames.bracketMatchId,
        "bracketId" -> bracketMatchWithGames.bracketId,
        "bracketRoundNr" -> bracketMatchWithGames.bracketRoundNr,
        "bracketMatchNr" -> bracketMatchWithGames.bracketMatchNr,
        "match" -> Json.toJson(bracketMatchWithGames.siteMatch)(siteMatchWrites)
      )

    }
    implicit val scoreWrites = Json.writes[PlayerScores]
    implicit val rankWrits = Json.writes[Rank]
    implicit val gameWrites = Json.writes[SiteGame]

    val robinGroupWrites = new Writes[RobinGroup] {
      override def writes(robinGroup: RobinGroup) = Json.obj(
        "robinGroupId" -> robinGroup.robinGroupId,
        "robinPlayers" -> robinGroup.robinPlayers.map(Json.toJson(_)(Json.writes[SeriesRoundPlayer])),
        "robinMatches" -> Json.listToJson(robinGroup.robinMatches)(siteMatchWrites)
      )
    }

    val fullBracketWrites: Writes[Bracket] = new Writes[Bracket] {
      override def writes(o: Bracket): JsValue = Json.obj(
        "bracketId" -> o.bracketId,
        "bracketPlayers" -> Json.toJson(o.bracketPlayers.map(Json.toJson(_)(Json.writes[BracketPlayer]))),
        "bracketRounds" -> Json.toJson(o.bracketRounds.map(round => Json.toJson(round.map(Json.toJson(_)(bracketMatchWrites)))))
      )
    }

    val fullRobinRoundWrites = new Writes[RobinRound] {
      override def writes(robinRound: RobinRound): JsValue = Json.obj(
        "robins" -> robinRound.robinList.map(Json.toJson(_)(robinGroupWrites))
      )

    }

    val fullRoundWrites = new Writes[SeriesRoundWithPlayersAndMatches] {
      override def writes(seriesRoundWithPlayersAndMatches: SeriesRoundWithPlayersAndMatches): JsValue  = seriesRoundWithPlayersAndMatches match{
      case bracket:Bracket => Json.toJson(bracket)(fullBracketWrites)
      case robin: RobinRound => Json.toJson(robin)(fullRobinRoundWrites)
    }
  }

  def drawSeriesOfTournament(tournamentId: String) = Action.async{

    seriesService.drawTournamentFirstRounds(tournamentId).map{ drawnRoundsResults =>
      val result = drawnRoundsResults.map{
        case Left(error) => Json.toJson(error)(Json.writes[DrawError])
        case Right(seriesRoundWithPlayersAndMatches) => Json.obj("seriesId" -> seriesRoundWithPlayersAndMatches._1,
                                                                  "round"   -> Json.toJson(seriesRoundWithPlayersAndMatches._2)(fullRoundWrites))

      }

      Ok(Json.toJson(result))
    }
  }
}
