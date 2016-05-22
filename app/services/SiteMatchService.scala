package services


import models.matches.SiteMatch

import scala.concurrent.{Future, Awaitable}
import scala.concurrent.ExecutionContext.Implicits.global


class SiteMatchService extends GenericAtomicCrudService[SiteMatch]{
  val siteMatches = List(
    SiteMatch("1","1","2","1",3,true,21,2,0,0)
  )

  def getMatch(matchId: String): Future[Option[SiteMatch]] = Future(siteMatches.find(siteMatch => siteMatch.matchId.contains(matchId)))

}
