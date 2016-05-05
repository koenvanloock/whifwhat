package services

import models.SiteMatch

import scala.concurrent.{Future, Awaitable}
import scala.concurrent.ExecutionContext.Implicits.global


class SiteMatchService extends GenericAtomicCrudService[SiteMatch]{
  val siteMatches = List(
    SiteMatch("1","1","2",3,true,21,2)
  )

  def getMatch(matchId: String): Future[Option[SiteMatch]] = Future(siteMatches.find(siteMatch => siteMatch.matchId.contains(matchId)))

}
