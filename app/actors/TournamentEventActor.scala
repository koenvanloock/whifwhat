package actors

import javax.inject.Inject

import actors.ActiveHall._
import actors.ActiveTournament._
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask
import models.halls._
import models.matches.{PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, RefereeInfo}
import utils.PingpongMatchUtils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._


object TournamentEventActor {

  case class SetChannel(streamActor: ActorRef)

  case class Hallchanged(hall: Hall)

  case object HallRemoved

  case object GetHall

  case class MoveMatchInHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)

  case class ActiveTournamentChanged(tournament: HallOverViewTournament)

  case object ActiveTournamentRemoved

  case object GetActiveTournament

  case object HasActiveTournament

  case class HallRefereeInsert(hallId: String, row: Int, column: Int, insertedReferee: Player)

  case class HallRefereeDelete(hallId: String, row: Int, column: Int, deletedReferee: Player, completed: Boolean)

  case class HallMatchDelete(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)

  case class RoundAdvance(roundMatches: List[PingpongMatch])

  case class MatchCompleted(pingpongMatch: PingpongMatch)

  case class MatchUpdated(matchWithSetResults: PingpongMatch)

  case class FreedHallPlayers(freePlayers: List[Player])

}

class TournamentEventActor @Inject()(implicit val system: ActorSystem) extends Actor {
  implicit val timeout = Timeout(5 seconds)

  import TournamentEventActor._

  private var activeHall = system.actorOf(ActiveHall.props)
  private val activeTournament = system.actorOf(ActiveTournament.props)



  override def receive: Receive = {
    case SetChannel(streamActor) =>
      activeHall ! ActiveHall.SetChannel(streamActor)
      activeTournament ! SetStreamActor(streamActor)
    case Hallchanged(newHall) => updateHall(newHall)
    case HallRemoved => activeHall ! RemoveActiveHall
    case MoveMatchInHall(hallId, row, column, pingpongMatch) => putMatchInHall(hallId, row, column, pingpongMatch)
    case GetHall => activeHall ! ActiveHall.GetHall(sender)
    case ActiveTournamentChanged(newTournament) =>
      val realSender = sender()
      (activeHall ? GetHallPlayers).mapTo[List[Player]].map{ occupiedPlayers =>
        activeTournament ! ActivateTournament(newTournament, occupiedPlayers, realSender)
      }
    case ActiveTournamentRemoved =>
      activeTournament ! RemoveTournament
      sender() ! "ok"
    case HallMatchDelete(hallId, row, column, pingpongMatch) =>
      activeHall ! ActiveHall.DeleteMatchInHall(hallId, row, column)
      val freePlayers = getMatchPlayers(pingpongMatch)
      activeTournament ! ActiveTournament.UpdateMatchInTournament(pingpongMatch, Nil, freePlayers)
    case MatchUpdated(matchWithSetResults: PingpongMatch) =>
      activeHall ! UpdateMatchInHall(matchWithSetResults)
      activeTournament ! UpdateMatchInTournament(matchWithSetResults, Nil)
    case MatchCompleted(pingpongMatch) =>
      (activeHall ? IsMatchComplete(pingpongMatch.id)).mapTo[Boolean].map( isComplete =>
        if(!isComplete) {
          (activeHall ? ActiveHall.GetReferee(pingpongMatch.id)).mapTo[Option[Player]].map(playerOpt => playerOpt.foreach(referee => activeTournament ! UpRefCount(referee)))
        })
      activeTournament ! CompleteMatchInTournament(pingpongMatch)
    case HallRefereeInsert(hallId, row, column, insertedReferee) =>
      activeHall ! ActiveHall.MoveRefereeToTable(hallId, row, column, insertedReferee)
      val occupiedPlayers = List(insertedReferee)
      activeTournament ! NewOccupiedPlayers(occupiedPlayers)
    case HallRefereeDelete(hallId, row, column, deletedReferee: Player, completed) =>
        activeHall ! ActiveHall.DeleteRefereeFromTable(hallId, row, column, deletedReferee)
        val freePlayers = List(deletedReferee)
        activeTournament ! NewOccupiedPlayers(Nil, freePlayers = freePlayers)

    case RoundAdvance(matches) => ???
    case GetActiveTournament =>
      val safeSender = sender()
      activeTournament ! ActiveTournament.GetActiveTournament(safeSender)
    case HasActiveTournament =>
      val safeSender = sender()
      activeTournament ! ActiveTournament.HasActiveTournament(safeSender)
    case FreedHallPlayers(freePlayers) => activeTournament ! NewOccupiedPlayers(Nil, freePlayers)
    case _ => sender() ! "method not accepted"
  }

  private def updateHall(newHall: Hall) = {
    activeHall ! ActivateHall(newHall, sender())
    activeTournament ! NewOccupiedPlayers(occupiedHallPlayers(newHall))
  }

  private def putMatchInHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch) = {
    activeHall ! MoveMatchToHall(hallId, row, column, pingpongMatch)
    activeTournament ! NewOccupiedPlayers(getMatchPlayers(pingpongMatch))
  }


  private def occupiedHallPlayers(hall: Hall): List[Player] = {
    hall.tables.flatMap(_.pingpongMatch.toList.flatMap(getMatchPlayers))
  }
}
