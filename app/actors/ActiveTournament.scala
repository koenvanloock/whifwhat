package actors

import actors.ActiveTournament.{HasActiveTournament, _}
import actors.StreamActor.PublishActiveTournament
import akka.actor.{Actor, ActorRef, Props}
import models.halls.HallOverViewTournament
import models.matches.{MatchChecker, PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, RefereeInfo, ViewablePlayer}

object ActiveTournament{

  def props: Props = Props(new ActiveTournament())

  case class SetStreamActor(streamActorRef: ActorRef)
  case class HasActiveTournament(senderRef: ActorRef)
  case class GetActiveTournament(senderRef: ActorRef)
  case object RemoveTournament
  case class ActivateTournament(tournament: HallOverViewTournament, occupiedPlayers: List[Player], senderRef: ActorRef)
  case class UpdateMatchInTournament(pingpongMatch: PingpongMatch, occupiedPlayers: List[Player])
  case class NewOccupiedPlayers(occupiedPlayers: List[Player])

}

class ActiveTournament extends Actor{
  import ActiveTournament._

  private var streamActor: Option[ActorRef] = None
  private var tournament: Option[HallOverViewTournament] = None

  override def receive: Receive = {
    case SetStreamActor(streamActorRef: ActorRef) => streamActor = Some(streamActorRef)
    case HasActiveTournament(senderRef: ActorRef) => senderRef ! tournament.nonEmpty
    case GetActiveTournament(senderRef: ActorRef) => senderRef ! tournament
    case ActivateTournament(newTournament, occupiedPlayers, senderRef) =>
      tournament = Some(newTournament)
      updatePlayers(occupiedPlayers)
      publishTournament()
      senderRef !  tournament
    case UpdateMatchInTournament(pingpongMatch, occupiedPlayers) =>
      updateMatchInList(pingpongMatch, occupiedPlayers)
      updatePlayers(occupiedPlayers)
      publishTournament()
    case RemoveTournament => tournament = None
    case NewOccupiedPlayers(occupiedPlayers) => tournament = tournament.map( realTournament => realTournament.copy(
      players = realTournament.players.map(viewPlayer => viewPlayer.copy(occupied = occupiedPlayers.contains(viewPlayer.player))),
      matchesToPlay = realTournament.matchesToPlay.map(updateOccupied(_,occupiedPlayers))
    ))
      publishTournament()
    case _ => sender() ! "invalid command"
  }

  private def updateMatchInList(updatedMatch: PingpongMatch, occupiedPlayers: List[Player]) =
    tournament = tournament.map( existingTournament => existingTournament.copy(matchesToPlay = newMatchOrViewableExistingMatch(updatedMatch, existingTournament, occupiedPlayers).reverse))

  private def updatePlayers(occupiedPlayers: List[Player]) = {
    tournament = tournament.map(realTournament => realTournament.copy( players = realTournament.players.map( player => ViewablePlayer( player.player, RefereeInfo(0,0), occupiedPlayers.contains(player.player)))))
  }

  private def publishTournament(): Unit = streamActor.foreach( realActorRef =>  tournament.foreach{
    realTournament =>
      realActorRef ! PublishActiveTournament(realTournament)
  })


  private def matchPlayersAreAvailable(pingpongMatch: ViewablePingpongMatch, occupiedPlayers: List[Player]): Boolean = {
    occupiedPlayers.forall(player =>
      pingpongMatch.pingpongMatch.playerA.exists(a => a.id != player.id) &&
        pingpongMatch.pingpongMatch.playerB.exists(b => b.id != player.id))
  }

  private def newMatchOrViewableExistingMatch(updatedMatch: PingpongMatch, tournament: HallOverViewTournament, occupiedPlayers: List[Player]): List[ViewablePingpongMatch] = {
    tournament.matchesToPlay.foldLeft(List[ViewablePingpongMatch]())((acc, pingpongMatch) => if(updatedMatch.id == pingpongMatch.pingpongMatch.id){
      ViewablePingpongMatch(MatchChecker.calculateSets(updatedMatch), MatchChecker.isWon(updatedMatch), false) :: acc
    }  else {
      updateOccupied(pingpongMatch, occupiedPlayers) :: acc
    })
  }

  private def updateOccupied(pingpongMatch: ViewablePingpongMatch, occupiedPlayers: List[Player]) = pingpongMatch.copy(isOccupied = !matchPlayersAreAvailable(pingpongMatch, occupiedPlayers))
}
