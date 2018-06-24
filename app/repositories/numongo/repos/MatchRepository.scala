package repositories.numongo.repos

import com.google.inject.Inject
import models.matches.{MatchFormat, PingpongMatch}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class MatchRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[PingpongMatch, String]("matches", MatchFormat.reads, MatchFormat.writes)(reactiveMongoApi){

}
