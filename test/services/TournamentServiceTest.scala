package services

import java.time.LocalDate

import models.Tournament
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await
import helpers.TestHelpers.DEFAULT_DURATION
/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 18:46
  */
class TournamentServiceTest extends PlaySpec{
  val tournamentService = new TournamentService()

  "TournamentService" should {
    "return a tournament by id" in {
      val tournament = Await.result(tournamentService.getTournament("1"), DEFAULT_DURATION)
      tournament mustBe Some(Tournament(Some("1"),"Kapels Kampioenschap", LocalDate.of(2016,9,8),2,true,true))
    }


    "create a tournament, it receives an id" in {
      val tournament = Await.result(tournamentService.createTournament(Tournament(None,"Dubbelkampioenschap", LocalDate.of(2016,6,24),2,false,false)), DEFAULT_DURATION).get
      tournament.tournamentId.get.length mustBe 36
      tournament.tournamentName mustBe "Dubbelkampioenschap"
    }

    "update a tournament" in {
      val createdTournament = Await.result(tournamentService.createTournament(Tournament(None,"Dubbelkampioenschap", LocalDate.of(2016,6,24),2,false,false)), DEFAULT_DURATION)
      val tournament = Await.result(tournamentService.updateTournament(Tournament(createdTournament.get.tournamentId,"Klaastornooi", LocalDate.of(2016,6,24),2,false,false)), DEFAULT_DURATION).get
      tournament.tournamentName mustBe "Klaastornooi"
    }

    "delete a tournament" in {
      Await.result(tournamentService.deleteTournament("2"), DEFAULT_DURATION)
      val tournament = Await.result(tournamentService.getTournament("2"), DEFAULT_DURATION)
      tournament mustBe None
    }
  }
}
