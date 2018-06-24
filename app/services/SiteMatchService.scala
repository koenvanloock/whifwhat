package services


import javax.inject.Inject

import models.matches.PingpongMatch
import repositories.numongo.repos.MatchRepository



class SiteMatchService @Inject()(matchRepository: MatchRepository){

  def getMatch(matchId: String) = matchRepository.findById(matchId)

  def create(siteMatch: PingpongMatch) = matchRepository.create(siteMatch)

  def update(siteMatch: PingpongMatch) = matchRepository.update(siteMatch)

  def delete(siteMatchId: String) = matchRepository.delete(siteMatchId)
}
