package actors

import actors.StreamActor.{PublishActiveHall, PublishActiveTournament}
import akka.actor.{Actor, ActorRef}
import models.halls._
import models.matches.{MatchChecker, PingpongMatch}
import models.player.Player

object TournamentEventActor {

  case class SetChannel(streamActor: ActorRef)

  case class Hallchanged(hall: Hall)

  case object HallRemoved

  case object GetHall

  case class HallMatchUpdate(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)

  case class ActiveTournamentChanged(tournament: HallOverViewTournament)

  case object ActiveTournamentRemoved

  case object GetActiveTournament

  case object HasActiveTournament

  case class HallMatchDelete(hallId: String, row: Int, column: Int)
  case class RoundAdvance(roundMatches: List[PingpongMatch])

}

class TournamentEventActor extends Actor {

  import TournamentEventActor._

  private var streamActorRef: Option[ActorRef] = None
  private var hall: Option[Hall] = None
  private var tournament: Option[HallOverViewTournament] = None
  private var occupiedPlayers: List[Player] = Nil
  private var uncompletedTournamentMatches: List[PingpongMatch] = Nil

  def tableOrTableWithoutMatch(pingpongMatch: PingpongMatch): (HallTable) => HallTable = hallTable => if (hallTable.siteMatch.contains(pingpongMatch)) hallTable.copy(siteMatch = None) else hallTable

  def removeHallMatch(pingpongMatch: PingpongMatch) = hall.foreach { existingHall =>
    this.hall = Some(existingHall.copy(tables = existingHall.tables.map(tableOrTableWithoutMatch(pingpongMatch))))
    streamActorRef.foreach(existingRef => existingRef ! PublishActiveHall(this.hall.get))
  }


  override def receive: Receive = {
    case SetChannel(streamActor) => streamActorRef = Some(streamActor)
    case Hallchanged(newHall) =>
      this.hall = Some(newHall)
      this.occupiedPlayers = Nil
      streamActorRef.foreach(streamActor => streamActor ! PublishActiveHall(newHall))
      sender ! hall
    case HallRemoved =>
      this.hall = None
      this.occupiedPlayers = Nil
    case HallMatchUpdate(hallId, row, column, pingpongMatch) => canPlayMatch(pingpongMatch)( pingpongMatch => {
      updateHallTournament(pingpongMatch, isInsert = true)
      updateHallMatch(hallId, row, column, pingpongMatch)
      Right(pingpongMatch)
    })

    case GetHall => sender ! hall
    case ActiveTournamentChanged(newTournament) =>
      tournament = Some(newTournament)
      this.uncompletedTournamentMatches = newTournament.matchesToPlay
      sender ! tournament
    case ActiveTournamentRemoved =>
      tournament = None
      sender() ! None
    case HallMatchDelete(hallId, row, column) => this.hall.map(existingHall =>
      if(existingHall.id == hallId){
        existingHall.tables.find(table => table.row == row && table.column == column).map(table =>  table.siteMatch.map{
          existingMatch => removeHallMatch(existingMatch)
            removeHallMatch(existingMatch)
            updateHallTournament(existingMatch, isInsert = false)
            Right(existingMatch)
        })

      } else { Left("wrong hall")}
    )
    case RoundAdvance(matches) => this.uncompletedTournamentMatches ++ matches
    case GetActiveTournament => sender ! tournament
    case HasActiveTournament => sender ! tournament.nonEmpty
    case _ => sender() ! "method not accepted"
  }

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

  def updateHallTournament(pingpongMatch: PingpongMatch, isInsert: Boolean): Unit = {
    tournament.foreach { existingTournament =>
      if(MatchChecker.isWon(pingpongMatch)){
        this.uncompletedTournamentMatches = uncompletedTournamentMatches.filterNot(_.id == pingpongMatch.id)
      }
      val playerList = if(isInsert) removeMatchPlayersFromList(pingpongMatch, existingTournament.freePlayers) else existingTournament.freePlayers ++ getMatchPlayers(pingpongMatch)
      this.occupiedPlayers = if(isInsert) this.occupiedPlayers ++ getMatchPlayers(pingpongMatch) else removeMatchPlayersFromList(pingpongMatch, this.occupiedPlayers)
      tournament = Some(existingTournament.copy(freePlayers = playerList, matchesToPlay = uncompletedTournamentMatches.filter(matchPlayersAreAvailable)))
      streamActorRef.foreach(existingRef => tournament.foreach(existingTournament => existingRef ! PublishActiveTournament(existingTournament)))
    }
  }

  def canPlayMatch(pingpongMatch: PingpongMatch)(pingpongMatchAction: PingpongMatch => Either[String,PingpongMatch]): Either[String, PingpongMatch] = {
    if(matchPlayersAreAvailable(pingpongMatch)){
        pingpongMatchAction(pingpongMatch)
    } else
      println("illegale match "+pingpongMatch)
      Left("Deze wedstrijd kan niet gespeeld worden")
  }

  private def matchPlayersAreAvailable(pingpongMatch: PingpongMatch): Boolean = {
    this.occupiedPlayers.forall(player =>
      pingpongMatch.playerA.exists(a => a.id != player.id) &&
      pingpongMatch.playerB.exists(b => b.id != player.id))
  }

  private def getMatchPlayers(pingpongMatch: PingpongMatch) = List(pingpongMatch.playerA, pingpongMatch.playerB).flatten

  private def removeMatchPlayersFromList(pingpongMatch: PingpongMatch, playerList: List[Player]): List[Player] = {
    playerList.filter( player =>  getMatchPlayers(pingpongMatch).forall( _.id != player.id))
  }
}
