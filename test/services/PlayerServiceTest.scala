package services

import models.player.{Player, Ranks}
import org.scalatestplus.play.PlaySpec
import helpers.TestHelpers._
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.mongo.{PlayerRepository, SeriesPlayerRepository, SeriesRepository}

import scala.concurrent.Await

class PlayerServiceTest extends PlaySpec {
  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesPlayerRepository = appBuilder.injector.instanceOf[SeriesPlayerRepository]
  val seriesRepository = appBuilder.injector.instanceOf[SeriesRepository]
  val playerRepository = appBuilder.injector.instanceOf[PlayerRepository]

  val playerService = new PlayerService(seriesPlayerRepository, seriesRepository,playerRepository)
  val player =  Await.result(playerService.createPlayer(Player("6", "Aram","Pauwels", Ranks.B4)),DEFAULT_DURATION)


  "PlayerService" should{
    "return a list of players" in {
      val createdPlayer = waitFor(playerRepository.create(Player("6","Aram", "Pauwels", Ranks.B4, None)))
      val players =  waitFor(playerService.getPlayers)
      players must contain(Player(createdPlayer.id, "Aram","Pauwels", Ranks.B4))
    }


    "create a player, he/she recieves an id" in {
      val createdPlayer = waitFor(playerRepository.create(Player("10","Aram", "Pauwels", Ranks.B4, None)))
      val player =  waitFor(playerService.getPlayer("10"))
      player mustBe Some(createdPlayer)
    }

    "update a player" in {
      val newPlayer = Await.result(playerService.updatePlayer(Player(player.id, "Koen","Pauwels", Ranks.B4)), DEFAULT_DURATION)
      newPlayer.firstname mustBe "Koen"
    }

    "delete a player" in {
      waitFor(playerService.deletePlayer("122"))
      val player =  waitFor(playerService.getPlayer("122"))
      player mustBe None
    }
  }

}
