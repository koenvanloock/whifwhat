package services


import javax.inject.Inject

import models.matches.SiteMatch
import repositories.mongo.MatchRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


class SiteMatchService @Inject()(matchRepository: MatchRepository){

  def getMatch(matchId: String) = matchRepository.retrieveById(matchId)

  def create(siteMatch: SiteMatch) = matchRepository.create(siteMatch)

  def update(siteMatch: SiteMatch) = matchRepository.update(siteMatch)

  def delete(siteMatchId: String) = matchRepository.delete(siteMatchId)
}
