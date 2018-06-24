package repositories.numongo.repos

import com.google.inject.Inject
import models.{Role, RoleFormat}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class RoleRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepository[Role, String]("roles", RoleFormat.reads, RoleFormat.writes)(reactiveMongoApi){

}
