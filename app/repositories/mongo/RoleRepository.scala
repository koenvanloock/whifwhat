package repositories.mongo

import javax.inject.Inject

import models.Role
import play.modules.reactivemongo.ReactiveMongoApi
import models.RoleEvidence._

class RoleRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[Role]("roles", "asc",10){

}
