package actors

import akka.actor.{Actor, Props}
import models.Tournament
import play.api.libs.iteratee.Concurrent

object TournamentEventStreamActor {
  def props = Props[TournamentEventStreamActor]

  case class Start(out: Concurrent.Channel[Tournament])
  case class ActivateTournament(tournament: Tournament)
}

class TournamentEventStreamActor extends Actor {
  import TournamentEventStreamActor._
  var out: Option[Concurrent.Channel[Tournament]] = None

  override def receive: Receive = {
    case Start(outChannel) => this.out = Some(outChannel)
    case ActivateTournament(tournament: Tournament) =>
      println("printing tournament to stream: " + tournament.tournamentName)
      this.out.foreach(_.push(tournament))

  }
}