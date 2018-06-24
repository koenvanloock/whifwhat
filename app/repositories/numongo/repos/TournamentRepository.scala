package repositories.numongo.repos

import com.google.inject.Inject
import models.{Tournament, TournamentFormat}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class TournamentRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[Tournament, String]("tournaments", TournamentFormat.reads, TournamentFormat.writes){
}
