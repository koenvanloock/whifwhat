package repositories.mongo

import javax.inject.Inject

import models.SeriesRound
import play.modules.reactivemongo.ReactiveMongoApi
import models.SeriesRoundEvidence._

class SeriesRoundRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[SeriesRound]("series_rounds","asc",10){

}
