package actors

import akka.actor.{Actor, Props}
import models.Tournament
import models.halls._
import models.matches.PingpongMatch
import models.player.Player

object TournamentEventActor{
  def props = Props[TournamentEventActor]


  case class Hallchanged(hall: Hall)
  case object HallRemoved
  case class HallMatchUpdate(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)

  case class ActiveTournamentChanged(tournament: HallOverViewTournament)
  case object ActiveTournamentRemoved

  case class MatchUpdate(pingpongMatch: PingpongMatch)
}

class TournamentEventActor extends Actor{
  import TournamentEventActor._

  private var hall: Option[Hall] = None
  private var tournament: Option[HallOverViewTournament] = None

  def tableOrTableWithoutMatch(pingpongMatch: PingpongMatch): (HallTable) => HallTable = hallTable => if(hallTable.siteMatch.contains(pingpongMatch)) hallTable.copy(siteMatch = None) else hallTable

  def removeHallMatch(pingpongMatch: PingpongMatch) = hall.foreach(existingHall => existingHall.copy(tables = existingHall.tables.map(tableOrTableWithoutMatch(pingpongMatch))))


  override def receive: Receive = {
    case Hallchanged(newHall) => this.hall = Some(newHall)
    case HallRemoved => this.hall = None
    case HallMatchUpdate(hallId, row, column, pingpongMatch) =>
                        updateHallTournament(pingpongMatch, getFreedPlayers(hallId, row, column))
                        updateHallMatch(hallId, row, column, pingpongMatch)
    case ActiveTournamentChanged(newTournament) => tournament = Some(newTournament)
    case ActiveTournamentRemoved => tournament = None
    case MatchUpdate(pingpongMatch) =>
      removeHallMatch(pingpongMatch)
      updateHallTournament(pingpongMatch, getMatchPlayers(pingpongMatch))

  }

  def getMatchPlayers(pingpongMatch: PingpongMatch) = List(pingpongMatch.playerA, pingpongMatch.playerB).flatten

  def updateHallMatch(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch): Unit = hall.foreach{
    existingHall =>
      if(existingHall.id == hallId){
        this.hall = Some(existingHall.copy(tables = existingHall.tables.map(tableOrTableWithMatch(row, column, pingpongMatch))))
      }
  }

  def tableOrTableWithMatch(row: Int, column: Int, pingpongMatch: PingpongMatch): HallTable => HallTable = table => if(table.row == row && table.column == column){
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

  def roundFilteredOut(pingpongMatch: PingpongMatch, rounds: List[HallOverviewRound]): List[HallOverviewRound] = rounds.map( individualRound => individualRound.copy(roundMatchesToPlay = individualRound.roundMatchesToPlay.filterNot(_ == pingpongMatch)))
  def filterMatchesToPlay(pingpongMatch: PingpongMatch, series: List[HallOverviewSeries]): scala.List[HallOverviewSeries] = series.map(individualSeries => individualSeries.copy(rounds = roundFilteredOut(pingpongMatch, individualSeries.rounds)))

  def updateHallTournament(pingpongMatch: PingpongMatch, freedPlayers: List[Player]): Unit = {
    tournament.foreach{ existingTournament =>
      val playerList = freedPlayers ++ existingTournament.freePlayers.filterNot(player => pingpongMatch.playerA.contains(player) || pingpongMatch.playerB.contains(player))
      tournament = Some(existingTournament.copy(freePlayers = playerList, series = filterMatchesToPlay(pingpongMatch, existingTournament.series)))
    }
  }
}
