package actors

import akka.actor.Actor
import models.halls.{Hall, HallOverViewTournament}
import play.api.libs.json.{JsObject, Json}
import play.api.libs.iteratee.Concurrent


object StreamActor{

  case class Start(channel: Concurrent.Channel[JsObject])
  case class PublishActiveTournament(hallOverViewTournament: HallOverViewTournament)
  case class PublishActiveHall(hall: Hall)

}

class StreamActor extends Actor{
  import StreamActor._
  import models.halls.HallEvidence._
  import jsonconverters.HallTournamentOverviewWrites._
  private var channel: Option[Concurrent.Channel[JsObject]] = None

  def receive: Receive = {
    case Start(outChannel: Concurrent.Channel[JsObject]) => this.channel = Some(outChannel)
    case PublishActiveTournament(tournament: HallOverViewTournament) =>
      println("printing tournament to stream: " + tournament.tournamentName)
      this.channel.foreach(_.push(Json.obj("tournament" -> Json.toJson(tournament))))

    case PublishActiveHall(hall: Hall) =>
      println("printing hall to stream: " + hall.hallName)
      this.channel.foreach(_.push(Json.obj("hall" -> Json.toJson(hall))))
  }
}
