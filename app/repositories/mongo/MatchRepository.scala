package repositories.mongo

import javax.inject.Inject

import models.matches.SiteMatch
import play.modules.reactivemongo.ReactiveMongoApi

import models.matches.MatchEvidence._

class MatchRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi) extends GenericMongoRepo[SiteMatch]("matches","asc",10){

}
