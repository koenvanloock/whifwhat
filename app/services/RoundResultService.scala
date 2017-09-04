package services

import javax.inject.Inject

import models.RoundResult
import repositories.mongo.RoundResultRepository

import scala.concurrent.Future

class RoundResultService @Inject()(roundResultRepository: RoundResultRepository){

  def create(roundResult: RoundResult): Future[RoundResult] = roundResultRepository.create(roundResult)

  def retrieveResultOfSeriesRound(roundId: String): Future[Option[RoundResult]] = roundResultRepository.retrieveByField("seriesRoundId", roundId)

}
