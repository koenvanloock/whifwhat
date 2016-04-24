package services

import models.{Ranks, TournamentSeries}
import org.scalatestplus.play.PlaySpec
import helpers.TestHelpers._

import scala.concurrent.Await

class SeriesServiceTest extends PlaySpec{


  val seriesService = new SeriesService()

  "TournamentSeriesService" should {
    "return a list of series of a tournament" in {

      val series = waitFor(seriesService.getTournamentSeriesOfTournament("1"))
      series must contain(TournamentSeries(Some("1"), "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1"))
    }

    "create a series, he/she recieves an id" in {
      val series = Await.result(seriesService.create(TournamentSeries(None, "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION)
      series.get.seriesId.get.length mustBe 36
    }

    "update a series" in {
      val createdTournamentSeries = Await.result(seriesService.create(TournamentSeries(None, "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION)
      val series = Await.result(seriesService.update(TournamentSeries(None, "Open zonder voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION).get
      series.seriesName mustBe "Open zonder voorgift"
    }

    "delete a series" in {
      waitFor(seriesService.delete("2"))
      val series = waitFor(seriesService.getTournamentSeries("2"))
      series mustBe None
    }
  }

}
