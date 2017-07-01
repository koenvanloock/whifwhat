package services


import javax.inject.Inject

import models.matches.PingpongMatch
import repositories.mongo.MatchRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class SiteMatchService @Inject()(matchRepository: MatchRepository){

  def getMatch(matchId: String) = matchRepository.retrieveById(matchId)

  def create(siteMatch: PingpongMatch) = matchRepository.create(siteMatch)

  def update(siteMatch: PingpongMatch) = matchRepository.update(siteMatch)

  def delete(siteMatchId: String) = matchRepository.delete(siteMatchId)
}
