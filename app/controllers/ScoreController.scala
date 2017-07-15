package controllers

import javax.inject.{Inject, Named}

import actors.ScoreActor
import akka.actor.ActorRef
import play.api.mvc.{Action, Controller}
import services.SeriesService

import scala.concurrent.ExecutionContext.Implicits.global

class ScoreController @Inject()(@Named("score-actor") scoreActor: ActorRef, seriesService: SeriesService) extends Controller{

  def startStreamingScores(tournamentId: String) = Action.async{
    seriesService.getTournamentSeriesOfTournament(tournamentId).map{seriesList =>
      scoreActor ! ScoreActor.StartTournamentScores(seriesList)
      NoContent
    }
  }

  def stopStreamingScores = Action{
    scoreActor ! ScoreActor.StopTournamentScores
    NoContent
  }
}
