package repositories.mongo

import javax.inject.Inject

import models.Tournament
import play.modules.reactivemongo.ReactiveMongoApi
import models.TournamentEvidence._

class TournamentRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[Tournament]("tournaments","asc",10){
}
