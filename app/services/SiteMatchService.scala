package services


import javax.inject.Inject

import models.matches.SiteMatch
import repositories.MatchRepository

import scala.concurrent.{Future, Awaitable}
import scala.concurrent.ExecutionContext.Implicits.global


class SiteMatchService @Inject()(matchRepository: MatchRepository){
  val siteMatches = List(
    SiteMatch("1","1","2","1",3,true,21,2,0,0)
  )

  def getMatch(matchId: String): Future[Option[SiteMatch]] = Future(siteMatches.find(siteMatch => siteMatch.matchId.contains(matchId)))


  def create(siteMatch: SiteMatch) = matchRepository.create(siteMatch)

  def update(siteMatch: SiteMatch) = matchRepository.update(siteMatch)

  def delete(siteMatchId: String) = matchRepository.delete(siteMatchId)
}
