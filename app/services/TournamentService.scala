package services

import java.time.LocalDate

import models.Tournament

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class TournamentService extends GenericAtomicCrudService[Tournament]{
  implicit val playerObject = models.Player

  val tournaments = List(
    Tournament(Some("1"),"Kapels Kampioenschap", LocalDate.of(2016,9,8),2,hasMultipleSeries = true,showClub = true)
  )

  def getTournament(tournamentId: String): Future[Option[Tournament]] = Future(tournaments.find(tournament => tournament.tournamentId.contains(tournamentId)))

  def createTournament(tournament: Tournament): Future[Option[Tournament]] = super.create(tournament)
  def updateTournament(tournament: Tournament): Future[Option[Tournament]] = super.update(tournament)
  def deleteTournament(tournamentId: String): Future[Option[String]] = super.delete(tournamentId)

}
