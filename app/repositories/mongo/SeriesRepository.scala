package repositories.mongo

import javax.inject.Inject

import models.TournamentSeries
import play.modules.reactivemongo.ReactiveMongoApi
import models.SeriesEvidence._

class SeriesRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[TournamentSeries]("series","asc",10){

}
