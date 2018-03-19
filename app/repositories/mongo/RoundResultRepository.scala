package repositories.mongo

import com.google.inject.Inject
import models.{RoundResult, RoundResultEvidence}
import play.modules.reactivemongo.ReactiveMongoApi
import models.RoundResultEvidence._

class RoundResultRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[RoundResult]("roundresults","asc",10, RoundResultEvidence){}
