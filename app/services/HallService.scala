package services

import javax.inject.Inject

import models.halls.{Hall, HallTable}
import models.matches.PingpongMatch
import play.api.libs.json.Json
import repositories.mongo.HallRepository

import scala.collection.immutable
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HallService @Inject()(hallRepository: HallRepository){

  def setMatchToTable(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch): Future[Option[Hall]] = {
    println(hallId)
    hallRepository.retrieveByField("id",hallId).map {
      case Some(hall) => addMatchToHall(hall, row, column, pingpongMatch)
      case _ => None
    }.flatMap{
      case Some(updatedHall) => println("updatedHall: " + updatedHall);hallRepository.update(updatedHall).map( hall => Some(hall))
      case _ => Future(None)
    }
  }

  protected def addMatchToHall(hall: Hall, row: Int, column: Int, pingpongMatch: PingpongMatch): Option[Hall] =
    Some(hall.copy(tables = hall.tables.map(originalOrMatchToInsert(row, column, pingpongMatch))))

  protected def originalOrMatchToInsert(row: Int, column: Int, pingpongMatch: PingpongMatch): (HallTable) => HallTable =
    hallTable => if(hallTable.row == row && hallTable.column == column) hallTable.copy( siteMatch = Some(pingpongMatch)) else hallTable


  def delete(hallId: String) = hallRepository.delete(hallId)

  def retrieveById(hallId: String) = hallRepository.retrieveById(hallId)

  def update(hall: Hall): Future[Hall] = hallRepository.update(hall)

  def getHallById(hallId: String) = hallRepository.retrieveById(hallId)

  def createIfNotExists(hall: Hall, isGreen: Boolean): Future[Either[String, Hall]] = hallRepository.retrieveByField("hallName", hall.hallName).flatMap{
    case Some(foundHall) => Future(Left(s"De opgegeven zaalnaam ${hall.hallName} bestaat al."))
    case None => hallRepository.create(hall.copy(tables = generateTables(hall.id, hall.rows, hall.tablesPerRow, isGreen))).map(Right(_))
  }


  def retrieveAll: Future[List[Hall]] = hallRepository.retrieveAll()


  def generateTables(hallId: String, rows:Int, columns:Int, isGreen: Boolean): List[HallTable] = {
    for{
      row <- 1 to rows
      col <- 1 to columns
    } yield HallTable(hallId, row, col, None, None, isGreen=isGreen)
  }.toList
}
