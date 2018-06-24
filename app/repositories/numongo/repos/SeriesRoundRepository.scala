package repositories.numongo.repos

import com.google.inject.Inject
import models.{SeriesRound, SeriesRoundFormat}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class SeriesRoundRepository @Inject()(reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[SeriesRound, String]("seriesrounds", SeriesRoundFormat.reads, SeriesRoundFormat.writes)(reactiveMongoApi){

}
