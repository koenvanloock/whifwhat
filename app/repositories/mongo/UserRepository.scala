package repositories.mongo

import javax.inject.Inject

import models.{User, UserEvidence}
import UserEvidence._
import play.modules.reactivemongo.ReactiveMongoApi


class UserRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[User]("users","asc",10){

}
