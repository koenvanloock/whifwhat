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


  val tournaments = List(
    Tournament("1","Kapels Kampioenschap", LocalDate.of(2016,9,8),2,hasMultipleSeries = true,showClub = true)
  )

  def getTournament(tournamentId: String): Future[Option[Tournament]] = Future(tournaments.find(tournament => tournament.id.contains(tournamentId)))

  def createTournament(tournament: Tournament): Future[Tournament] = tournamentRepository.create(tournament)
  def updateTournament(tournament: Tournament): Future[Tournament] = tournamentRepository.update(tournament)
  def deleteTournament(tournamentId: String): Future[Unit] = tournamentRepository.delete(tournamentId)

}
