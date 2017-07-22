package actors

import javax.inject.{Inject, Named}

import akka.actor.{Actor, ActorRef}
import models.TournamentSeries
import play.api.libs.json.Json
import services.SeriesRoundService
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object ScoreActor {

  case class StartTournamentScores(seriesList: List[TournamentSeries], interval: Int=30)
  case class ShowNextActiveRound(series: TournamentSeries)
  case object StopTournamentScores
}


class ScoreActor @Inject()(@Named("score-streamer") scoreStreamer: ActorRef, seriesRoundService: SeriesRoundService)  extends Actor{
  import ScoreActor._
  var killSignal = false


  override def receive: Receive = {
    case StopTournamentScores => killSignal = true;
    case StartTournamentScores(seriesList, interval) => streamScores(seriesList, interval)
    case ShowNextActiveRound(series: TournamentSeries) => showActiveRoundOf(series)
    case _ => sender() !  "method not supported"
  }

  private def streamScores(seriesList: List[TournamentSeries], interval: Int) = {
    context.system.scheduler.scheduleOnce(interval seconds, self, ShowNextActiveRound(seriesList.head))
    if(!killSignal) {
      val editedList = moveHeadToBack(seriesList)
      context.system.scheduler.scheduleOnce(interval seconds, self, StartTournamentScores(editedList, interval))
    } else {
      killSignal = false
    }
  }

  private def moveHeadToBack(seriesList: List[TournamentSeries]): List[TournamentSeries] = {
    seriesList.tail ++ List(seriesList.head)
  }

  def showActiveRoundOf(series: TournamentSeries) = {

    seriesRoundService.retrieveByFields(Json.obj("seriesId" -> series.id, "roundNr" -> series.currentRoundNr)).map{
      case Some(round) => scoreStreamer ! ScoreStreamer.ShowRound(series.seriesName, series.seriesColor, round)
      case _ => ()
    }

  }
}
