package repositories.mongo

import javax.inject.Inject

import models.halls.{Hall, HallEvidence}
import play.modules.reactivemongo.ReactiveMongoApi

class HallRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[Hall]("halls","asc",10, HallEvidence){}
