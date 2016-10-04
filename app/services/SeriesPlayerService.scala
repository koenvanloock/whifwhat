package services

import javax.inject.Inject

import models.player.SeriesPlayer
import repositories.mongo.SeriesPlayerRepository

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesPlayerService @Inject()(seriesPlayerRepository: SeriesPlayerRepository){
  def subscribePlayers(seriesPlayers: List[SeriesPlayer]):Future[List[SeriesPlayer]] = Future.sequence( seriesPlayers.map(seriesPlayerRepository.create))


}
