package services

import java.time.LocalDate
import javax.inject.Inject

import models.Tournament
import models.player.Player
import play.api.libs.json.{JsValue, Json, Writes}
import repositories.mongo.TournamentRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class TournamentService @Inject()(tournamentRepository: TournamentRepository){
  implicit val playerObject = Player



  def createTournament(tournament: Tournament): Future[Tournament] = tournamentRepository.create(tournament)
  def updateTournament(tournament: Tournament): Future[Tournament] = tournamentRepository.update(tournament)
  def deleteTournament(tournamentId: String): Future[Unit] = tournamentRepository.delete(tournamentId)

}
