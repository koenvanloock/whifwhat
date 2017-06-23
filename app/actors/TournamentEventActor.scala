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
  case class MoveMatchInHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)
  case class ActiveTournamentChanged(tournament: HallOverViewTournament)
  case object ActiveTournamentRemoved
  case object GetActiveTournament
  case object HasActiveTournament
  case class HallRefereeInsert(hallId: String, row: Int, column: Int, insertedReferee: Player)
  case class HallRefereeDelete(hallId: String, row: Int, column: Int, deletedReferee: Player)
  case class HallMatchDelete(hallId: String, row: Int, column: Int)
  case class RoundAdvance(roundMatches: List[PingpongMatch])

}

class TournamentEventActor extends Actor {

  import TournamentEventActor._

  private var streamActorRef: Option[ActorRef] = None
  private var hall: Option[Hall] = None
  private var tournament: Option[HallOverViewTournament] = None

  private var tournamentPlayers: List[Player] = Nil
  private var occupiedPlayers: List[Player] = Nil
  private var uncompletedTournamentMatches: List[PingpongMatch] = Nil


  override def receive: Receive = {
    case SetChannel(streamActor) => streamActorRef = Some(streamActor)
    case Hallchanged(newHall) => activateNewHall(newHall)
    case HallRemoved => clearHall
    case MoveMatchInHall(hallId, row, column, pingpongMatch) => moveMatchToHall(hallId, row, column, pingpongMatch)

    case GetHall => sender ! hall
    case ActiveTournamentChanged(newTournament) => activateTournament(newTournament)
    case ActiveTournamentRemoved => clearTournament
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
    case HallRefereeInsert(hallId, row, column, insertedReferee) =>
      updateHallwith(hallId)(insertReferee(row, column, insertedReferee))
      updateTournamentWith(addRefereeToOccupied(insertedReferee))
    case HallRefereeDelete(hallId, row, column, deletedReferee: Player) =>
      updateHallwith(hallId)(deleteRef(row, column))
      updateTournamentWith(freeRefereeFromOccupied(deletedReferee))
    case RoundAdvance(matches) => this.uncompletedTournamentMatches ++ matches
    case GetActiveTournament => sender ! tournament.map( existingTournament => existingTournament.copy(freePlayers = tournamentPlayers.filterNot(occupiedPlayers.contains)))
    case HasActiveTournament => sender ! tournament.nonEmpty
    case _ => sender() ! "method not accepted"
  }

  private def clearTournament = {
    tournament = None
    occupiedPlayers = Nil
    uncompletedTournamentMatches = Nil
    sender() ! None
  }

  private def moveMatchToHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch) = {
    canPlayMatch(pingpongMatch)(pingpongMatch => {
      updateHallTournament(pingpongMatch, isInsert = true)
      updateHallwith(hallId)(tableOrTableWithMatch(row, column, pingpongMatch))
      Right(pingpongMatch)
    })
  }

  private def activateTournament(newTournament: HallOverViewTournament) = {
    this.tournament = Some(newTournament)
    this.tournamentPlayers = newTournament.freePlayers
    updateTournamentWith(addTournament)
    sender ! tournament.map(applyOccupiedFilters)
  }

  private def clearHall = {
    this.hall = None
    this.occupiedPlayers = Nil
  }

  private def activateNewHall(newHall: Hall) = {
    this.hall = Some(newHall)
    this.occupiedPlayers = newHall.tables.flatMap(table => table.siteMatch.toList.flatMap(getMatchPlayers) ++ table.referee.toList)
    updateTournamentWith(applyOccupiedFilters)
    streamActorRef.foreach(streamActor => streamActor ! PublishActiveHall(newHall))
    sender ! hall
  }

  def addTournament: HallOverViewTournament => HallOverViewTournament = (hallOverViewTournament) => {
    this.uncompletedTournamentMatches = hallOverViewTournament.matchesToPlay
    this.occupiedPlayers = hall.map(existingHall => existingHall.tables.flatMap(table => table.siteMatch.toList.flatMap(getMatchPlayers))).getOrElse(Nil)
    hallOverViewTournament.copy(
      freePlayers = hallOverViewTournament.freePlayers.filterNot(player => occupiedPlayers.contains(player)),
      matchesToPlay = uncompletedTournamentMatches.filter(matchPlayersAreAvailable)
    )
  }

  def applyOccupiedFilters: HallOverViewTournament => HallOverViewTournament = hallOverviewTournament => {
    hallOverviewTournament.copy(
      freePlayers =tournamentPlayers.filterNot(occupiedPlayers.contains(_)),
      matchesToPlay = uncompletedTournamentMatches.filter(matchPlayersAreAvailable)
    )
  }

  def updateHallwith(hallId: String)(updateTableFunc: HallTable => HallTable): Unit = hall.foreach {
    existingHall =>
      if (existingHall.id == hallId) {
        this.hall = Some(existingHall.copy(tables = existingHall.tables.map(updateTableFunc)))
      }
      streamActorRef.foreach(existingRef => existingRef ! PublishActiveHall(this.hall.get))

  }

  def tableOrTableWithMatch(row: Int, column: Int, pingpongMatch: PingpongMatch): HallTable => HallTable = table => if (table.row == row && table.column == column) {
    table.copy(siteMatch = Some(pingpongMatch))
  } else table

  def deleteRef(row: Int, column: Int): HallTable => HallTable = table => if (table.row == row && table.column == column) {
    table.copy(referee = None)
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

  def updateTournamentWith(updateFunc: HallOverViewTournament => HallOverViewTournament) = {
    this.tournament = tournament.map(updateFunc)
    streamActorRef.foreach(existingRef => tournament.foreach(existingTournament => existingRef ! PublishActiveTournament(existingTournament)))
  }

  def canPlayMatch(pingpongMatch: PingpongMatch)(pingpongMatchAction: PingpongMatch => Either[String,PingpongMatch]): Either[String, PingpongMatch] = {
    if(matchPlayersAreAvailable(pingpongMatch)){
        pingpongMatchAction(pingpongMatch)
    } else
      println("illegale match "+pingpongMatch)
      Left("Deze wedstrijd kan niet gespeeld worden")
  }

  def tableOrTableWithoutMatch(pingpongMatch: PingpongMatch): (HallTable) => HallTable = hallTable => if (hallTable.siteMatch.contains(pingpongMatch)) hallTable.copy(siteMatch = None) else hallTable

  def insertReferee(row: Int, column: Int, insertedReferee: Player): (HallTable) =>HallTable = table => {
    if (table.row == row && table.column == column) {
      table.copy(referee =  Some(insertedReferee))
    } else table
  }

  def removeHallMatch(pingpongMatch: PingpongMatch) = hall.foreach { existingHall =>
    this.hall = Some(existingHall.copy(tables = existingHall.tables.map(tableOrTableWithoutMatch(pingpongMatch))))
    streamActorRef.foreach(existingRef => existingRef ! PublishActiveHall(this.hall.get))
  }


  def freeRefereeFromOccupied(deletedReferee: Player): (HallOverViewTournament) => HallOverViewTournament = tournament => {
    this.occupiedPlayers = this.occupiedPlayers.filterNot(_ == deletedReferee)
    updateActiveTournament(tournament)
  }

  def addRefereeToOccupied(insertedReferee: Player): (HallOverViewTournament) => HallOverViewTournament = tournament => {
    this.occupiedPlayers = insertedReferee :: this.occupiedPlayers
    updateActiveTournament(tournament)
  }

  private def updateActiveTournament(tournament: HallOverViewTournament) = {
    tournament.copy(freePlayers = tournamentPlayers.filterNot(this.occupiedPlayers.contains(_)), matchesToPlay = uncompletedTournamentMatches.filter(matchPlayersAreAvailable))
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
