package services

import javax.inject.Inject

import models.RoundResult
import repositories.numongo.repos.RoundResultRepository
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RoundResultService @Inject()(roundResultRepository: RoundResultRepository){
  def createOrUpdate(result: RoundResult) = {
    roundResultRepository.findFirstByField("roundId", result.seriesRoundId).map{
      case Some(roundResult) => roundResultRepository.update(result.copy(id = roundResult.id))
      case None => roundResultRepository.create(result)
    }
  }

  def create(roundResult: RoundResult): Future[RoundResult] = roundResultRepository.create(roundResult)

  def retrieveResultOfSeriesRound(roundId: String): Future[Option[RoundResult]] = roundResultRepository.findFirstByField("seriesRoundId", roundId)

}
