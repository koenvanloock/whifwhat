package controllers

import javax.inject.{Inject, Named}

import actors.ScoreActor
import akka.actor.ActorRef
import play.api.mvc.{Action, AnyContent, Controller, InjectedController}
import services.SeriesService

import scala.concurrent.ExecutionContext.Implicits.global

class ScoreController @Inject()(@Named("score-actor") scoreActor: ActorRef, seriesService: SeriesService) extends InjectedController{

  def startStreamingScores(tournamentId: String): Action[AnyContent] = Action.async{
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
