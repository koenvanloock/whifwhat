package repositories.mongo

import javax.inject.Inject

import models.matches.PingpongMatch
import play.modules.reactivemongo.ReactiveMongoApi

import models.matches.MatchEvidence._

class MatchRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[PingpongMatch]("matches","asc",10){

}
