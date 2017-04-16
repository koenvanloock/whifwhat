package repositories.mongo

import javax.inject.Inject

import models.halls.Hall
import play.modules.reactivemongo.ReactiveMongoApi
import models.halls.HallEvidence._

class HallRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[Hall]("halls","asc",10){}
