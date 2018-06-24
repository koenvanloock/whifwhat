package services

import java.time.LocalDate

import helpers.TestHelpers.DEFAULT_DURATION
import models.Tournament
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatestplus.play.PlaySpec
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.numongo.repos.TournamentRepository

import scala.concurrent.Await

@RunWith(classOf[JUnitRunner])
class TournamentServiceTest extends PlaySpec{
  val appBuilder = new GuiceApplicationBuilder().build()
  val tournamentRepository = appBuilder.injector.instanceOf[TournamentRepository]
  val tournamentService = new TournamentService(tournamentRepository)

  "TournamentService" should {


    "create a tournament, it receives an id" in {
      val tournament = Await.result(tournamentService.createTournament(Tournament("","Dubbelkampioenschap", LocalDate.of(2016,6,24),2,false,false)), DEFAULT_DURATION)
      tournament.tournamentName mustBe "Dubbelkampioenschap"
    }

    "update a tournament" in {
      val createdTournament = Await.result(tournamentService.createTournament(Tournament("223","Dubbelkampioenschap", LocalDate.of(2016,6,24),2,false,false)), DEFAULT_DURATION)
      val tournament = Await.result(tournamentService.updateTournament(Tournament(createdTournament.id,"Klaastornooi", LocalDate.of(2016,6,24),2,false,false)), DEFAULT_DURATION)
      tournament.tournamentName mustBe "Klaastornooi"
    }
  }
}
