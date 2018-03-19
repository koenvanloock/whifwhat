package repositories.mongo

import javax.inject.Inject

import models.{Role, RoleEvidence}
import play.modules.reactivemongo.ReactiveMongoApi

class RoleRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[Role]("roles", "asc",10, RoleEvidence){

}
