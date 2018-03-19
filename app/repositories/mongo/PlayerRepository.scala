package repositories.mongo

import com.google.inject.Inject
import models.player.{Player, PlayerEvidence}
import play.modules.reactivemongo.ReactiveMongoApi
import models.player.PlayerEvidence._

class PlayerRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[Player]("players","asc",10, PlayerEvidence){

}
