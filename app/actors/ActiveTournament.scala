package actors

import actors.ActiveTournament.{HasActiveTournament, _}
import actors.StreamActor.PublishActiveTournament
import akka.actor.{Actor, ActorRef, Props}
import models.halls.HallOverViewTournament
import models.matches.{MatchChecker, PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, RefereeInfo, ViewablePlayer}
import utils.PingpongMatchUtils._

object ActiveTournament {

  def props: Props = Props(new ActiveTournament())

  case class SetStreamActor(streamActorRef: ActorRef)

  case class HasActiveTournament(senderRef: ActorRef)

  case class GetActiveTournament(senderRef: ActorRef)

  case object RemoveTournament

  case class ActivateTournament(tournament: HallOverViewTournament, occupiedPlayers: List[Player], senderRef: ActorRef)

  case class UpdateMatchInTournament(pingpongMatch: PingpongMatch, newOccupiedPlayers: List[Player]=Nil, newFreePlayers: List[Player]=Nil)

  case class NewOccupiedPlayers(occupiedPlayers: List[Player]=Nil, freePlayers: List[Player]=Nil)

  case object GetMatchesToPlay

  case class UpRefCount(referee: Player)

  case class IsMatchComplete(matchId: String)

}

class ActiveTournament extends Actor {

  import ActiveTournament._

  private var streamActor: Option[ActorRef] = None
  private var tournament: Option[HallOverViewTournament] = None

  override def receive: Receive = {
    case SetStreamActor(streamActorRef: ActorRef) => streamActor = Some(streamActorRef)
    case HasActiveTournament(senderRef: ActorRef) => senderRef ! tournament.nonEmpty
    case GetActiveTournament(senderRef: ActorRef) => senderRef ! tournament
    case ActivateTournament(newTournament, occupiedPlayers, senderRef) => activateTournament(newTournament, occupiedPlayers, senderRef)
    case UpdateMatchInTournament(pingpongMatch, newOccupiedPlayers, freePlayers) =>
      tournament = tournament.map( realTournament => realTournament.copy(players = updatePlayerList(realTournament.players, newOccupiedPlayers, freePlayers)))
      updateMatchInList(pingpongMatch)
      publishTournament()
    case RemoveTournament => tournament = None
    case NewOccupiedPlayers(newOccupied, newFreed) =>
      tournament = tournament.map{realTournament => realTournament.copy(players = updatePlayerList(realTournament.players, newOccupied, newFreed))}
      tournament = tournament.map{realTournament => realTournament.copy(matchesToPlay = realTournament.matchesToPlay.map(updateOccupied))}
      publishTournament()
    case GetMatchesToPlay => sender() ! tournament.map(trueTournament => trueTournament.matchesToPlay).getOrElse(Nil)
    case UpRefCount(referee) => tournament = tournament.map(realTournament => realTournament.copy(players = updateIfInPlayerList(realTournament.players, List(referee))(viewPlayer => viewPlayer.copy(refereeInfo = viewPlayer.refereeInfo.copy(numberOfRefs = viewPlayer.refereeInfo.numberOfRefs + 1)))))
    case IsMatchComplete(matchId) => sender() ! withTournament( tournament => tournament.matchesToPlay.find(_.pingpongMatch.id == matchId).forall(_.isWon))(true)
    case _ => sender() ! "invalid command"
  }

  private def freeGivenPlayers(freePlayers: List[Player]) = {
    tournament = tournament.map(realTournament => realTournament.copy(players = updatePlayerList(realTournament.players, Nil, freePlayers)))
  }

  private def activateTournament(newTournament: HallOverViewTournament, occupiedPlayers: List[Player], senderRef: ActorRef) = {
    tournament = Some(newTournament)
    updatePlayers(occupiedPlayers)
    tournament = tournament.map(existingTournament => existingTournament.copy(matchesToPlay =  newTournament.matchesToPlay.map(updateOccupied)))
    publishTournament()
    senderRef ! tournament
  }

  private def updateMatchInList(updatedMatch: PingpongMatch) = {
    tournament = tournament.map { existingTournament =>
      if (MatchChecker.isWon(updatedMatch)) {
        val matchPlayers = getMatchPlayers(updatedMatch)
        val updatedPlayerList = updateIfInPlayerList(existingTournament.players, matchPlayers)( viewPlayer => viewPlayer.copy(occupied = false, refereeInfo = viewPlayer.refereeInfo.copy(matchesToPlay = viewPlayer.refereeInfo.matchesToPlay - 1)))
        existingTournament.copy(matchesToPlay = newMatchOrViewableExistingMatch(updatedMatch, existingTournament).reverse, players = updatedPlayerList)
      } else {
        existingTournament.copy(matchesToPlay = newMatchOrViewableExistingMatch(updatedMatch, existingTournament).reverse)
      }
    }
  }

  private def updatePlayers(occupiedPlayers: List[Player]) = {
    tournament = tournament.map(realTournament => realTournament.copy(players = realTournament.players.map(player => if(occupiedPlayers.contains(player.player)) ViewablePlayer(player.player, player.refereeInfo, true) else player)))
  }

  private def publishTournament(): Unit = streamActor.foreach(realActorRef => tournament.foreach {
    realTournament =>
      realActorRef ! PublishActiveTournament(realTournament)
  })


  private def matchPlayersAreAvailable(pingpongMatch: ViewablePingpongMatch): Boolean = {
    occupiedPlayers.forall(viewablePlayer =>
      pingpongMatch.pingpongMatch.playerA.exists(a => a.id != viewablePlayer.player.id) &&
        pingpongMatch.pingpongMatch.playerB.exists(b => b.id != viewablePlayer.player.id))
  }

  private def newMatchOrViewableExistingMatch(updatedMatch: PingpongMatch, tournament: HallOverViewTournament): List[ViewablePingpongMatch] = {
    tournament.matchesToPlay.foldLeft(List[ViewablePingpongMatch]())((acc, pingpongMatch) => if (updatedMatch.id == pingpongMatch.pingpongMatch.id) {
      ViewablePingpongMatch(MatchChecker.calculateSets(updatedMatch), MatchChecker.isWon(updatedMatch), false) :: acc
    } else {
      updateOccupied(pingpongMatch) :: acc
    })
  }

  private def updateOccupied(pingpongMatch: ViewablePingpongMatch) = pingpongMatch.copy(isOccupied = !matchPlayersAreAvailable(pingpongMatch))

  private def occupiedPlayers = withTournament(realTournament => realTournament.players.filter(_.occupied))(Nil)

  private def withTournament[T](f: HallOverViewTournament => T)(default: T) = this.tournament.map(f).getOrElse(default)

  private def updatePlayerList(playerList: List[ViewablePlayer], newOccupied: List[Player], newFreed: List[Player]) = playerList.foldLeft(Nil: List[ViewablePlayer])((acc, viewPlayer) => {
    if (newOccupied.contains(viewPlayer.player)) {
      viewPlayer.copy(occupied = true) :: acc
    } else if (newFreed.contains(viewPlayer.player)) {
      viewPlayer.copy(occupied = false) :: acc
    } else {
      viewPlayer :: acc
    }
  }).reverse

  private def updateIfInPlayerList(playerList: List[ViewablePlayer], playersToUpdate: List[Player])(updateFunc: ViewablePlayer => ViewablePlayer) = playerList.foldLeft(Nil: List[ViewablePlayer])((acc, viewPlayer) => {
    if (playersToUpdate.contains(viewPlayer.player)) {
      updateFunc(viewPlayer) :: acc
    } else {
      viewPlayer :: acc
    }
  }).reverse
}
