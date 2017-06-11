package actors

import akka.actor.{Actor, Props}
import models.halls.HallOverViewTournament
import play.api.libs.iteratee.Concurrent

object TournamentEventStreamActor {
  def props = Props[TournamentEventStreamActor]

  case class Start(out: Concurrent.Channel[HallOverViewTournament])
  case class ActivateTournament(tournament: HallOverViewTournament)
}

class TournamentEventStreamActor extends Actor {
  import TournamentEventStreamActor._
  var out: Option[Concurrent.Channel[HallOverViewTournament]] = None

  override def receive: Receive = {
    case Start(outChannel) => this.out = Some(outChannel)
    case ActivateTournament(tournament: HallOverViewTournament) =>
      //println("printing tournament to stream: " + tournament.tournamentName)
      this.out.foreach(_.push(tournament))

  }
}