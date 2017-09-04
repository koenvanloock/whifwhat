package repositories.mongo

import javax.inject.Inject

import models.RoundResult
import play.modules.reactivemongo.ReactiveMongoApi
import models.RoundResultEvidence._

class RoundResultRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[RoundResult]("roundResults","asc",10){}
