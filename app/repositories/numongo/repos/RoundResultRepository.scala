package repositories.numongo.repos

import com.google.inject.Inject
import models.{RoundResult, RoundResultFormat}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class RoundResultRepository @Inject()(reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[RoundResult, String]("roundresults", RoundResultFormat.reads, RoundResultFormat.writes)(reactiveMongoApi){

}
