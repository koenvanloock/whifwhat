package services

import models.{Ranks, Player}
import org.scalatestplus.play.PlaySpec
import helpers.TestHelpers._

import scala.concurrent.Await

class PlayerServiceTest extends PlaySpec {
  val playerService = new PlayerService()

  "PlayerService" should{
    "return a list of players" in {

      val players =  waitFor(playerService.getPlayers)
      players must contain(Player(None, "Aram","Pauwels", Ranks.B4))
    }


    "create a player, he/she recieves an id" in {

      val player =  Await.result(playerService.createPlayer(Player(None, "Aram","Pauwels", Ranks.B4)),DEFAULT_DURATION)
        player.get.playerId.get.length mustBe 36
    }

    "update a player" in {
      val createdPlayer = Await.result(playerService.createPlayer(Player(None, "Aram","Pauwels", Ranks.B4)), DEFAULT_DURATION)
      val player = Await.result(playerService.updatePlayer(Player(createdPlayer.get.playerId, "Koen","Pauwels", Ranks.B4)), DEFAULT_DURATION).get
      player.firstname mustBe "Koen"
    }

    "delete a player" in {
      waitFor(playerService.deletePlayer("2"))
      val player =  waitFor(playerService.getPlayer("2"))
      player mustBe None
    }
  }

}
