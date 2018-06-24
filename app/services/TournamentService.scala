package services

import javax.inject.Inject

import models.Tournament
import models.player.Player
import repositories.numongo.repos.TournamentRepository

import scala.concurrent.Future


class TournamentService @Inject()(tournamentRepository: TournamentRepository){
  implicit val playerObject = Player



  def createTournament(tournament: Tournament): Future[Tournament] = tournamentRepository.create(tournament)
  def updateTournament(tournament: Tournament): Future[Tournament] = tournamentRepository.update(tournament)
  def deleteTournament(tournamentId: String): Future[Option[Tournament]] = tournamentRepository.delete(tournamentId)

}
