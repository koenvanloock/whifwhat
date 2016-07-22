package services

import models.player.{Ranks, Player}
import org.scalatestplus.play.PlaySpec
import helpers.TestHelpers._
import play.api.inject.guice.GuiceApplicationBuilder
import repositories.{Schema, PlayerRepository, SeriesRepository, SeriesPlayerRepository}

import scala.concurrent.Await

class PlayerServiceTest extends PlaySpec {
  val appBuilder = new GuiceApplicationBuilder().build()
  val seriesPlayerRepository = appBuilder.injector.instanceOf[SeriesPlayerRepository]
  val seriesRepository = appBuilder.injector.instanceOf[SeriesRepository]
  val playerRepository = appBuilder.injector.instanceOf[PlayerRepository]
  val schema = appBuilder.injector.instanceOf[Schema]
  schema.initSchema()

  val playerService = new PlayerService(seriesPlayerRepository, seriesRepository,playerRepository)
  val player =  Await.result(playerService.createPlayer(Player("6", "Aram","Pauwels", Ranks.B4)),DEFAULT_DURATION)


  "PlayerService" should{
    "return a list of players" in {

      val players =  waitFor(playerService.getPlayers)
      players must contain(Player("6", "Aram","Pauwels", Ranks.B4))
    }


    "create a player, he/she recieves an id" in {

        player.get.playerId.length mustBe 1
    }

    "update a player" in {
      val newPlayer = Await.result(playerService.updatePlayer(Player(player.get.playerId, "Koen","Pauwels", Ranks.B4)), DEFAULT_DURATION).get
      newPlayer.firstname mustBe "Koen"
    }

    "delete a player" in {
      waitFor(playerService.deletePlayer("122"))
      val player =  waitFor(playerService.getPlayer("122"))
      player mustBe None
    }
  }

}
