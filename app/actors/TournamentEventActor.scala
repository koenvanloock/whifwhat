package actors

import javax.inject.Inject

import actors.ActiveHall.{ActivateHall, MoveMatchToHall, RemoveActiveHall, UpdateMatchInHall}
import actors.ActiveTournament._
import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.util.Timeout
import akka.pattern.ask
import models.halls._
import models.matches.{PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, RefereeInfo}

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

}

class TournamentEventActor @Inject()(implicit val system: ActorSystem) extends Actor {
  implicit val timeout = Timeout(5 seconds)

  import TournamentEventActor._

  private var activeHall = system.actorOf(ActiveHall.props)
  private val activeTournament = system.actorOf(ActiveTournament.props)

  private var occupiedPlayers: List[Player] = Nil
  private var playerInfo: Map[Player, RefereeInfo] = Map()



  override def receive: Receive = {
    case SetChannel(streamActor) =>
      activeHall ! ActiveHall.SetChannel(streamActor)
      activeTournament ! SetStreamActor(streamActor)
    case Hallchanged(newHall) => updateHall(newHall)
    case HallRemoved => activeHall ! RemoveActiveHall
    case MoveMatchInHall(hallId, row, column, pingpongMatch) =>
      withInvolvedFreePlayers(getMatchPlayers(pingpongMatch))(putMatchInHall(hallId, row, column, pingpongMatch))
    case GetHall => activeHall ! ActiveHall.GetHall(sender)
    case ActiveTournamentChanged(newTournament) =>
      populatePlayerInfo(newTournament)
      activeTournament ! ActivateTournament(newTournament, occupiedPlayers, sender())
    case ActiveTournamentRemoved =>
      activeTournament ! RemoveTournament
      sender() ! "ok"
    case HallMatchDelete(hallId, row, column, pingpongMatch) =>
      activeHall ! ActiveHall.DeleteMatchInHall(hallId, row, column)
      freePlayers(getMatchPlayers(pingpongMatch))
      activeTournament ! ActiveTournament.UpdateMatchInTournament(pingpongMatch, occupiedPlayers)
    case MatchUpdated(matchWithSetResults: PingpongMatch) =>
      activeHall ! UpdateMatchInHall(matchWithSetResults)
      activeTournament ! UpdateMatchInTournament(matchWithSetResults, occupiedPlayers)
    case MatchCompleted(pingpongMatch) =>

    case HallRefereeInsert(hallId, row, column, insertedReferee) =>
      activeHall ! ActiveHall.MoveRefereeToTable(hallId, row, column, insertedReferee)
      occupyPlayers(List(insertedReferee))
      activeTournament ! NewOccupiedPlayers(occupiedPlayers)
    case HallRefereeDelete(hallId, row, column, deletedReferee: Player, completed) =>
      if(completed) upRefereeCount(deletedReferee)
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

  private def updateHall(newHall: Hall) = {
    activeHall ! ActivateHall(newHall, sender())
    occupyPlayers(occupiedHallPlayers(newHall))
    activeTournament ! NewOccupiedPlayers(occupiedPlayers)
  }

  private def putMatchInHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch) = {
    activeHall ! MoveMatchToHall(hallId, row, column, pingpongMatch) // todo what if there is a match on target??? block or overwrite?
    occupyPlayers(getMatchPlayers(pingpongMatch))
    activeTournament ! NewOccupiedPlayers(occupiedPlayers)
  }

  private def getMatchPlayers(pingpongMatch: PingpongMatch): List[Player] =
      pingpongMatch.playerA.toList ++ pingpongMatch.playerB.toList


  private def occupyPlayers(playerList: List[Player]) = occupiedPlayers = occupiedPlayers ++ playerList

  private def freePlayers(playerList: List[Player]) = occupiedPlayers = occupiedPlayers.filterNot(playerList.contains)

  private def occupiedHallPlayers(hall: Hall): List[Player] = {
    hall.tables.flatMap(_.pingpongMatch.toList.flatMap(getMatchPlayers))
  }

  private def withInvolvedFreePlayers(players: List[Player])(f: Unit) = if (occupiedPlayers.forall(occupiedPlayer => !players.contains(occupiedPlayer))) f

  private def upRefereeCount(player: Player) {
    playerInfo.get(player) match {
      case None => playerInfo += (player -> RefereeInfo(1,0))
      case Some(refereeInfo) => playerInfo += (player -> refereeInfo.copy( numberOfRefs = refereeInfo.numberOfRefs + 1))
    }
  }

  private def populatePlayerInfo(newTournament: HallOverViewTournament) = {
      playerInfo = Map()
    newTournament.players.foreach{ player => playerInfo += player.player -> player.refereeInfo}
  }

  private def playerCompletedMatch(completedMatch: PingpongMatch) = {
    (activeTournament ? GetMatchesToPlay).mapTo[List[ViewablePingpongMatch]]
      .map{ matches =>
        val exists = matches.exists( viewMatch => viewMatch.pingpongMatch.id == completedMatch.id)
        if(exists){
          activeHall ! ActiveHall.DeleteMatchById(completedMatch.id, completed = true)
          freePlayers(getMatchPlayers(completedMatch))
          activeTournament ! ActiveTournament.UpdateMatchInTournament(completedMatch, occupiedPlayers)
          getMatchPlayers(completedMatch).foreach(decreasePlayerMatchesToPlay)}
      }
  }

  private def decreasePlayerMatchesToPlay(player: Player): Unit = {
    playerInfo.get(player) match {
      case Some(refereeInfo) => playerInfo += player -> refereeInfo.copy( matchesToPlay = refereeInfo.matchesToPlay - 1)
      case _ => playerInfo += player -> RefereeInfo(0,0)
    }
  }
}
