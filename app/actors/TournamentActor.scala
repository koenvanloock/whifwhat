package actors
import akka.actor._
import models.Tournament


object TournamentActor {
  def props = Props[TournamentActor]

  case class LoadTournament(tournament: Tournament)
  case object GetActiveTournament
  case object ReleaseTournament
  case object HasActiveTournament
}




class TournamentActor extends Actor{
  var activeTournament: Option[Tournament] = None

  import TournamentActor._
  override def receive: Receive = {
    case GetActiveTournament => sender() ! activeTournament
    case ReleaseTournament => {
      activeTournament = None
      sender() !  activeTournament
    }
    case LoadTournament(tournament) => {
       if(activeTournament.nonEmpty){
         sender() ! Left{ if(activeTournament.exists(actorTournament => actorTournament.id != tournament.id)) {
           "There is an active tournament. Please close that if you want to load another one!"
         } else {
           "This tournament is already loaded!"}
         }
       } else {
         activeTournament = Some(tournament)
        sender() ! Right(tournament)
       }
    }
    case HasActiveTournament => sender() ! activeTournament.nonEmpty

  }
}