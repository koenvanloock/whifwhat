package actors

import actors.StreamActor.{PublishActiveHall, PublishActiveTournament}
import akka.actor.{Actor, ActorRef, Props}
import models.halls._
import models.matches.PingpongMatch
import models.player.Player

object TournamentEventActor {
  def props = Props[TournamentEventActor]

  case class SetChannel(streamActor: ActorRef)

  case class Hallchanged(hall: Hall)

  case object HallRemoved

  case object GetHall

  case class HallMatchUpdate(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)

  case class ActiveTournamentChanged(tournament: HallOverViewTournament)

  case object ActiveTournamentRemoved

  case object GetActiveTournament

  case object HasActiveTournament

  case class MatchUpdate(pingpongMatch: PingpongMatch)

}

class TournamentEventActor extends Actor {

  import TournamentEventActor._

  private var streamActorRef: Option[ActorRef] = None
  private var hall: Option[Hall] = None
  private var tournament: Option[HallOverViewTournament] = None

  def tableOrTableWithoutMatch(pingpongMatch: PingpongMatch): (HallTable) => HallTable = hallTable => if (hallTable.siteMatch.contains(pingpongMatch)) hallTable.copy(siteMatch = None) else hallTable

  def removeHallMatch(pingpongMatch: PingpongMatch) = hall.foreach(existingHall => existingHall.copy(tables = existingHall.tables.map(tableOrTableWithoutMatch(pingpongMatch))))


  override def receive: Receive = {
    case SetChannel(streamActor) => streamActorRef = Some(streamActor)
    case Hallchanged(newHall) =>
      this.hall = Some(newHall)
      streamActorRef.foreach(streamActor => streamActor ! PublishActiveHall(newHall))
      sender ! hall
    case HallRemoved => this.hall = None
    case HallMatchUpdate(hallId, row, column, pingpongMatch) =>
      updateHallTournament(pingpongMatch, getFreedPlayers(hallId, row, column))
      updateHallMatch(hallId, row, column, pingpongMatch)
    case GetHall => sender ! hall
    case ActiveTournamentChanged(newTournament) => tournament = Some(newTournament)
      sender ! tournament
    case ActiveTournamentRemoved => tournament = None; sender() ! None
    case MatchUpdate(pingpongMatch) =>
      removeHallMatch(pingpongMatch)
      updateHallTournament(pingpongMatch, getMatchPlayers(pingpongMatch))
    case GetActiveTournament => sender ! tournament
    case HasActiveTournament => sender ! tournament.nonEmpty

  }

  def getMatchPlayers(pingpongMatch: PingpongMatch) = List(pingpongMatch.playerA, pingpongMatch.playerB).flatten

  def updateHallMatch(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch): Unit = hall.foreach {
    existingHall =>
      if (existingHall.id == hallId) {
        this.hall = Some(existingHall.copy(tables = existingHall.tables.map(tableOrTableWithMatch(row, column, pingpongMatch))))
      }
      streamActorRef.foreach(existingRef => existingRef ! PublishActiveHall(this.hall.get))

  }

  def tableOrTableWithMatch(row: Int, column: Int, pingpongMatch: PingpongMatch): HallTable => HallTable = table => if (table.row == row && table.column == column) {
    table.copy(siteMatch = Some(pingpongMatch))
  } else table

  def getFreedPlayers(hallId: String, row: Int, column: Int): List[Player] = hall.flatMap { existingHall =>
    if (existingHall.id == hallId) {
      existingHall.tables.find(table => table.row == row && table.column == column).flatMap { hallTable =>
        for {
          ref <- hallTable.referee
          playerA <- hallTable.siteMatch.flatMap(_.playerA)
          playerB <- hallTable.siteMatch.flatMap(_.playerB)
        } yield List(ref, playerA, playerB)
      }
    } else None
  }.getOrElse(Nil)

  def updateHallTournament(pingpongMatch: PingpongMatch, freedPlayers: List[Player]): Unit = {
    tournament.foreach { existingTournament =>
      val playerList = freedPlayers ++ existingTournament.freePlayers.filterNot(player => pingpongMatch.playerA.contains(player) || pingpongMatch.playerB.contains(player))
      tournament = Some(existingTournament.copy(freePlayers = playerList, matchesToPlay = existingTournament.matchesToPlay.filterNot(matchToFilter => matchToFilter.id != pingpongMatch.id)))
      streamActorRef.foreach(existingRef => tournament.foreach(existingTournament => existingRef ! PublishActiveTournament(existingTournament)))
    }
  }
}
