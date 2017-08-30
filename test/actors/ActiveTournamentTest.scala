import java.time.LocalDate

import actors.ActiveTournament
import actors.ActiveTournament._
import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import models.halls.HallOverViewTournament
import models.matches.{PingpongGame, PingpongMatch, ViewablePingpongMatch}
import models.player.{Player, Ranks, RefereeInfo, ViewablePlayer}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

@RunWith(classOf[JUnitRunner])
class ActiveTournamentTest extends TestKit(ActorSystem("MySpec")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "An ActiveTournament actor" must {
    val HANS = Player("1","Hans", "Van Bael", Ranks.E0, None)
    val ARAM = Player("2","Aram", "Pauwels", Ranks.B2, None)
    val KOEN = Player("3","Koen", "Van Loock", Ranks.D0, None)
    val HANS_ARAM = PingpongMatch("1", Some(HANS), Some(ARAM), "123", 10, false,21,2,0,0, Nil)
    val ARAM_KOEN = PingpongMatch("2", Some(KOEN), Some(ARAM), "123", 4, false,21,2,0,0, Nil)
    val HANS_KOEN = PingpongMatch("3", Some(HANS), Some(KOEN), "123", 7, true,21,2,0,0, Nil)

    val occupiedPlayers = List(HANS, ARAM)
    val matchList = List(HANS_ARAM, ARAM_KOEN, HANS_KOEN).map(ViewablePingpongMatch(_,false,false))
    val players = List(HANS, ARAM, KOEN).map(ViewablePlayer(_,RefereeInfo(0,0), false))
    val tournament = HallOverViewTournament("1234", "blabla",LocalDate.now(), Nil, players, matchList)

    "return false initially when no tournament has been set" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      activeTournament ! HasActiveTournament(this.testActor)
      expectMsg(false)
    }

    "return true when a tournament has been set" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      activeTournament ! ActivateTournament(tournament, Nil, this.testActor)
      activeTournament ! HasActiveTournament(this.testActor)
      expectMsg(Some(tournament))
      expectMsg(true)
    }

    "return None when retrieving active tournament when none is set" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      activeTournament ! GetActiveTournament(this.testActor)
      expectMsg(None)
    }

    "return the tournament when retrieving an existing active tournament" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      activeTournament ! ActivateTournament(tournament, Nil, this.testActor)
      activeTournament ! GetActiveTournament(this.testActor)
      expectMsg(Some(tournament))
      expectMsg(Some(tournament))
    }

    "remove the active tournament then retuns None on retrieval" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      activeTournament ! ActivateTournament(tournament, Nil, this.testActor)
      activeTournament ! RemoveTournament
      activeTournament ! GetActiveTournament(this.testActor)
      expectMsg(Some(tournament))
      expectMsg(None)
    }

    "respond to not implemented message" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      activeTournament ! "blabla"
      expectMsg("invalid command")
    }


    "updating a match updates isWon, the scores and the occupiedplayers, match frees occupied" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      val pingpongMatch = PingpongMatch("1", Some(HANS), Some(ARAM), "123", 10, false,21,2,0,2, List(PingpongGame(16,21,1), PingpongGame(18,21,2), PingpongGame(0,0,3)))
      activeTournament ! ActivateTournament(tournament, Nil, this.testActor)
      expectMsg(Some(tournament))
      activeTournament ! UpdateMatchInTournament(pingpongMatch, occupiedPlayers)
      val updatedMatchList = List(ViewablePingpongMatch(pingpongMatch,true,false), ViewablePingpongMatch(ARAM_KOEN,false,true), ViewablePingpongMatch(HANS_KOEN,false,true))
      val updatedPlayers = List(ViewablePlayer(HANS, RefereeInfo(0,0), false), ViewablePlayer(ARAM, RefereeInfo(0,0), false), ViewablePlayer(KOEN, RefereeInfo(0,0), false))
      val newTournament = HallOverViewTournament("1234", "blabla",LocalDate.now(), Nil, updatedPlayers, updatedMatchList)

      activeTournament ! GetActiveTournament(this.testActor)
      expectMsg(Some(newTournament))
    }

    "updating a match updates isWon, the scores and the occupiedplayers" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      val pingpongMatch = PingpongMatch("2", Some(KOEN), Some(ARAM), "123", 10, false,21,2,0,2, List(PingpongGame(16,21,1), PingpongGame(18,21,2), PingpongGame(0,0,3)))
      activeTournament ! ActivateTournament(tournament, Nil, this.testActor)
      expectMsg(Some(tournament))
      activeTournament ! UpdateMatchInTournament(pingpongMatch, occupiedPlayers)
      val updatedMatchList = List(ViewablePingpongMatch(HANS_ARAM,false,true), ViewablePingpongMatch(pingpongMatch,true,false), ViewablePingpongMatch(HANS_KOEN,false,true))
      val updatedPlayers = List(ViewablePlayer(HANS, RefereeInfo(0,0), true), ViewablePlayer(ARAM, RefereeInfo(0,0), false), ViewablePlayer(KOEN, RefereeInfo(0,0), false))
      val newTournament = HallOverViewTournament("1234", "blabla",LocalDate.now(), Nil, updatedPlayers, updatedMatchList)

      activeTournament ! GetActiveTournament(this.testActor)
      expectMsg(Some(newTournament))
    }

    "intruducing new occupied players updates them and updates the isOccupied of concerning matches" in {
      val activeTournament = system.actorOf(ActiveTournament.props)
      val newOccupiedPlayers = List(KOEN)
      activeTournament ! ActivateTournament(tournament, Nil, this.testActor)
      expectMsg(Some(tournament))
      activeTournament ! NewOccupiedPlayers(newOccupiedPlayers)
      val updatedMatchList = List(ViewablePingpongMatch(HANS_ARAM,false,false), ViewablePingpongMatch(ARAM_KOEN,false,true), ViewablePingpongMatch(HANS_KOEN,false,true))
      val updatedPlayers = List(ViewablePlayer(HANS, RefereeInfo(0,0), false), ViewablePlayer(ARAM, RefereeInfo(0,0), false), ViewablePlayer(KOEN, RefereeInfo(0,0), true))
      val updatedTournament = tournament.copy(players = updatedPlayers, matchesToPlay = updatedMatchList)

      activeTournament ! GetActiveTournament(this.testActor)
      expectMsg(Some(updatedTournament))
    }
  }
}
