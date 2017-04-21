package services

import javax.inject.Inject

import models.halls.{Hall, HallTable}
import repositories.mongo.HallRepository

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HallService @Inject()(hallRepository: HallRepository){
  def retrieveById(hallId: String) = hallRepository.retrieveById(hallId)

  def update(hall: Hall): Future[Hall] = hallRepository.update(hall)

  def getHallById(hallId: String) = hallRepository.retrieveById(hallId)

  def createIfNotExists(hall: Hall): Future[Either[String, Hall]] = hallRepository.retrieveByField("hallName", hall.hallName).flatMap{
    case Some(foundHall) => Future(Left(s"De opgegeven zaalnaam ${hall.hallName} bestaat al."))
    case None => hallRepository.create(hall.copy(tables = generateTables(hall.rows, hall.tablesPerRow))).map(Right(_))
  }


  def retrieveAll: Future[List[Hall]] = hallRepository.retrieveAll()


  def generateTables(rows:Int, columns:Int): List[HallTable] = {
    for{
      row <- 1 to rows
      col <- 1 to columns
    } yield HallTable(row, col, None, None)
  }.toList
}
