package actors

import javax.inject.Inject

import actors.ActiveHall.{ActivateHall, MoveMatchToHall, RemoveActiveHall}
import actors.ActiveTournament._
import actors.StreamActor.{PublishActiveHall, PublishActiveTournament}
import akka.actor.{Actor, ActorRef, ActorSystem}
import models.halls._
import models.matches.{MatchChecker, PingpongMatch}
import models.player.Player
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


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
  case class HallRefereeDelete(hallId: String, row: Int, column: Int, deletedReferee: Player)
  case class HallMatchDelete(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)
  case class RoundAdvance(roundMatches: List[PingpongMatch])
  case class MatchCompleted(pingpongMatch: PingpongMatch)

}

class TournamentEventActor @Inject()(implicit val system: ActorSystem) extends Actor {
  implicit val timeout = Timeout(5 seconds)

  import TournamentEventActor._

  private var activeHall = system.actorOf(ActiveHall.props)
  private val activeTournament = system.actorOf(ActiveTournament.props)

  private var occupiedPlayers: List[Player] = Nil

  override def receive: Receive = {
    case SetChannel(streamActor) =>
      activeHall ! ActiveHall.SetChannel(streamActor)
      activeTournament ! SetStreamActor(streamActor)
    case Hallchanged(newHall) =>
      activeHall ! ActivateHall(newHall, sender())
      occupyPlayers(occupiedHallPlayers(newHall))
      activeTournament ! NewOccupiedPlayers(occupiedPlayers)
    case HallRemoved => activeHall ! RemoveActiveHall
    case MoveMatchInHall(hallId, row, column, pingpongMatch) =>
      activeHall ! MoveMatchToHall(hallId, row, column, pingpongMatch)
      occupyPlayers(getMatchPlayers(pingpongMatch))
      activeTournament ! NewOccupiedPlayers(occupiedPlayers)
    case GetHall => activeHall ! ActiveHall.GetHall(sender)
    case ActiveTournamentChanged(newTournament) => activeTournament ! ActivateTournament(newTournament, occupiedPlayers, sender())
    case ActiveTournamentRemoved => activeTournament ! RemoveTournament
    case HallMatchDelete(hallId, row, column, pingpongMatch) =>
      activeHall ! ActiveHall.DeleteMatchInHall(hallId, row, column)
      freePlayers(getMatchPlayers(pingpongMatch))
      activeTournament ! ActiveTournament.UpdateMatchInTournament(pingpongMatch, occupiedPlayers)
    case MatchCompleted(pingpongMatch) =>
      activeHall ! ActiveHall.DeleteMatchById(pingpongMatch.id)
      freePlayers(getMatchPlayers(pingpongMatch))
      activeTournament ! ActiveTournament.UpdateMatchInTournament(pingpongMatch, occupiedPlayers)
    case HallRefereeInsert(hallId, row, column, insertedReferee) =>
      activeHall ! ActiveHall.MoveRefereeToTable(hallId, row, column, insertedReferee)
      occupyPlayers(List(insertedReferee))
      activeTournament ! NewOccupiedPlayers(occupiedPlayers)
    case HallRefereeDelete(hallId, row, column, deletedReferee: Player) =>
      activeHall ! ActiveHall.DeleteRefereeFromTable(hallId, row, column, deletedReferee)
      freePlayers(List(deletedReferee))
      activeTournament ! NewOccupiedPlayers(occupiedPlayers)

    case RoundAdvance(matches) => ???
    case GetActiveTournament =>
      val safeSender = sender()
      activeTournament ! ActiveTournament.GetActiveTournament(safeSender)
    case HasActiveTournament =>
      val safeSender = sender()
      activeTournament ! ActiveTournament.HasActiveTournament(safeSender)
    case _ => sender() ! "method not accepted"
  }

  private def getMatchPlayers(pingpongMatch: PingpongMatch): List[Player] = {

    val players = pingpongMatch.playerA.toList ++ pingpongMatch.playerB.toList
    println(players)
    players
  }
  private def occupyPlayers(playerList: List[Player]) = occupiedPlayers = occupiedPlayers ++ playerList

  private def freePlayers(playerList: List[Player]) = occupiedPlayers = occupiedPlayers.filterNot(playerList.contains)
  private def occupiedHallPlayers(hall: Hall): List[Player] = {
    hall.tables.flatMap(_.pingpongMatch.toList.flatMap(getMatchPlayers))
  }
}
