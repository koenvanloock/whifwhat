package controllers

import javax.inject.Inject

import models.matches.{SiteMatchWithGames, SiteGame, BracketMatchWithGames}
import models.player._
import models._
import play.api.libs.json.{JsValue, Writes, Json}
import play.api.mvc.{Action, Controller}
import services.{SeriesService, DrawService}
import scala.concurrent.ExecutionContext.Implicits.global

class DrawController @Inject()(seriesService: SeriesService) extends Controller{

    val matchWithGameWrites = new Writes[BracketMatchWithGames] {
      override def writes(bracketMatchWithGames: BracketMatchWithGames): JsValue = Json.obj(
        "bracketMatchId" -> bracketMatchWithGames.bracketMatchId,
        "bracketId" -> bracketMatchWithGames.bracketId,
        "bracketRoundNr" -> bracketMatchWithGames.bracketRoundNr,
        "bracketMatchNr" -> bracketMatchWithGames.bracketMatchNr,
        "matchId" -> bracketMatchWithGames.matchId,
        "playerA" -> bracketMatchWithGames.playerA,
        "playerB" -> bracketMatchWithGames.playerB,
        "handicap" -> bracketMatchWithGames.handicap,
        "isHandicapForB" -> bracketMatchWithGames.isHandicapForB,
        "targetScore" -> bracketMatchWithGames.targetScore,
        "numberOfSetsToWin" -> bracketMatchWithGames.numberOfSetsToWin,
        "sets" -> bracketMatchWithGames.sets.map(Json.toJson(_)(Json.writes[SiteGame]))
      )

    }
    implicit val scoreWrites = Json.writes[PlayerScores]
    implicit val rankWrits = Json.writes[Rank]
    implicit val gameWrites = Json.writes[SiteGame]

    val robinGroupWrites = new Writes[RobinGroup] {
      override def writes(robinGroup: RobinGroup) = Json.obj(
        "robinGroupId" -> robinGroup.robinGroupId,
        "robinPlayers" -> robinGroup.robinPlayers.map(Json.toJson(_)(Json.writes[SeriesRoundPlayer])),
        "robinMatches" -> robinGroup.robinMatches.map(Json.toJson(_)(Json.writes[SiteMatchWithGames]))
      )
    }

    val fullBracketWrites: Writes[Bracket] = new Writes[Bracket] {
      override def writes(o: Bracket): JsValue = Json.obj(
        "bracketId" -> o.bracketId,
        "bracketPlayers" -> Json.toJson(o.bracketPlayers.map(Json.toJson(_)(Json.writes[BracketPlayer]))),
        "bracketRounds" -> Json.toJson(o.bracketRounds.map(round => Json.toJson(round.map(Json.toJson(_)(matchWithGameWrites)))))
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
