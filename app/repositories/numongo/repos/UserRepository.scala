package repositories.numongo.repos

import com.google.inject.Inject
import models.{User, UserFormat}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class UserRepository @Inject()(reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[User, String]("users", UserFormat.reads, UserFormat.writes)(reactiveMongoApi){

}
