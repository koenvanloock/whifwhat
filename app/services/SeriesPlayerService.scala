package services

import javax.inject.Inject

import models.player.{PlayerScores, SeriesPlayer}
import repositories.mongo.{SeriesPlayerRepository, SeriesRoundRepository}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class SeriesPlayerService @Inject()(seriesPlayerRepository: SeriesPlayerRepository){
  def retrievePlayersOf(seriesIds: List[String]): Future[List[SeriesPlayer]] = {
    Future.sequence(seriesIds.map(seriesPlayerRepository.retrieveAllSeriesPlayers))
      .map(_.flatten)
  }

  def subscribePlayers(seriesPlayers: List[SeriesPlayer]):Future[List[SeriesPlayer]] = Future.sequence( seriesPlayers.map(seriesPlayerRepository.create))

}
