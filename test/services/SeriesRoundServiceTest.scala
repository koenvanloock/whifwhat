package services

import helpers.TestHelpers._
import models.RobinRound
import org.scalatestplus.play.PlaySpec

import scala.concurrent.Await


class SeriesRoundServiceTest extends PlaySpec{

  val seriesRoundService = new SeriesRoundService()

  "SeriesRoundService" should {
    "return a single seriesRound" in {

      val series = waitFor(seriesRoundService.getSeriesRound("1"))
      series must contain(RobinRound(Some("1"),2,"1",1))
    }

    /*
    "create a seriesRound, he/she recieves an id" in {
      val series = Await.result(seriesRoundService.create(RobinRound(Some("1"),2,"1",1)), DEFAULT_DURATION)
      series.get.seriesId.get.length mustBe 36
    }

    "update a series" in {
      val createdTournamentSeries = Await.result(seriesService.create(TournamentSeries(None, "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION)
      val series = Await.result(seriesService.update(TournamentSeries(None, "Open zonder voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION).get
      series.seriesName mustBe "Open zonder voorgift"
    }*/

    "delete a seriesRound" in {
      // insert 5 here once integration works
      waitFor(seriesRoundService.delete("5"))
      val series = waitFor(seriesRoundService.getSeriesRound("5"))
      series mustBe None
    }
  }
}
