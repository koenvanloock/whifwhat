package repositories.numongo.repos

import com.google.inject.Inject
import models.player.{Player, PlayerFormat}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class PlayerRepository @Inject()(reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[Player, String]("players", PlayerFormat.reads, PlayerFormat.writes)(reactiveMongoApi){

}
