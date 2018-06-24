package repositories.numongo.repos

import com.google.inject.Inject
import models.halls.Hall
import models.halls.HallFormat
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class HallRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[Hall, String]("halls", HallFormat.reads, HallFormat.writes)(reactiveMongoApi){

}
