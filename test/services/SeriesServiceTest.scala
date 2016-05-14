package services

import models._
import org.scalatestplus.play.PlaySpec
import helpers.TestHelpers._

import scala.concurrent.Await

class SeriesServiceTest extends PlaySpec{

  val players=  List(
    Player("1", "Koen","Van Loock", Ranks.D0 ),
    Player("2", "Hans","Van Bael", Ranks.E4 ),
    Player("3", "Luk","Geraets", Ranks.D6 ),
    Player("4", "Lode","Van Renterghem", Ranks.E6 ),
    Player("5", "Tim","Firquet", Ranks.C2 ),
    Player("6", "Aram","Pauwels", Ranks.B4 ),
    Player("7", "Tim","Uitdewilligen", Ranks.E0 ),
    Player("8", "Matthias","Lesuise", Ranks.D6 ),
    Player("9", "Gil","Corrujeira-Figueira", Ranks.D0 )
  )
  val seriesService = new SeriesService(new DrawService)

  "TournamentSeriesService" should {
    "return a list of series of a tournament" in {

      val series = waitFor(seriesService.getTournamentSeriesOfTournament("1"))
      series must contain(TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1"))
    }

    "create a series, he/she recieves an id" in {
      val series = Await.result(seriesService.create(TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION)
      series.get.seriesId.length mustBe 36
    }

    "update a series" in {
      val createdTournamentSeries = Await.result(seriesService.create(TournamentSeries("1", "Open met voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION)
      val series = Await.result(seriesService.update(TournamentSeries("1", "Open zonder voorgift","#ffffff", 2,21,true,0,true,0,"1")), DEFAULT_DURATION).get
      series.seriesName mustBe "Open zonder voorgift"
    }

    "delete a series" in {
      waitFor(seriesService.delete("2"))
      val series = waitFor(seriesService.getTournamentSeries("2"))
      series mustBe None
    }

    "draw a series" in {
     val test=  seriesService.drawSeries(SiteRobinRound("1234",2,"1",1), players,2,21).get
      test match {
        case robinRound : RobinRound => robinRound.robinList.head.robinPlayers.map(_.seriesPlayerId) mustBe List("1","3","5","7","9")
        case _ => fail("NO robinround found")
      }
    }

    "draw a bracket" in {
      val test=  seriesService.drawSeries(SiteBracketRound("1",2,"1",2), players,2,21).get
      test match {
        case bracket : Bracket =>
          bracket.bracketRounds.length mustBe 2
          bracket.bracketPlayers.length mustBe 4
          val firstRoundMatches = bracket.bracketRounds.head
          firstRoundMatches.head.playerA mustBe Some("1")
          firstRoundMatches.last.playerB mustBe Some("2")
        case _ => fail("NO bracketRound found")
      }
    }
  }

}
