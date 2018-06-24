package services

import javax.inject.Inject

import models.halls.{Hall, HallTable}
import models.matches.PingpongMatch
import models.player.Player
import repositories.numongo.repos.HallRepository
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class HallService @Inject()(hallRepository: HallRepository) {
  def deleteMatchAndRefInHall(hallId: String, pingpongMatch: PingpongMatch) = hallRepository.findById(hallId).flatMap {
    case Some(realHall) => update(updateTable(realHall)(table => if (table.pingpongMatch.exists(_.id == pingpongMatch.id)) table.copy(pingpongMatch = None, referee = None) else table)).map(Some(_))
    case _ => Future(None)
}

  def updateMatchInHall(hallId: String, pingpongMatch: PingpongMatch): Future[Option[Hall]] = {
    hallRepository.findById(hallId).flatMap {
      case Some(hallToUpdate) =>
        val newHall = hallToUpdate.copy(tables  = hallToUpdate.tables.map{ table => if(table.pingpongMatch.exists(_.id == pingpongMatch.id)) table.copy(pingpongMatch = Some(pingpongMatch)) else table })
        hallRepository.update(newHall).map(Some(_))
      case None => Future(None)
    }
  }

  def insertRefInHall(hallId: String, row: Int, column: Int, referee: Player): Future[Option[Hall]] = {
    hallRepository.findById(hallId).map { hallOpt =>
      val newHallOpt = addRefereeToHall(row, column, referee)(hallOpt)
      newHallOpt.map(newHall => hallRepository.update(newHall))
      newHallOpt
    }
  }

  def deleteRefereeOn(row: Int, column: Int, referee: Player): (Option[Hall]) => Option[Hall] = hallOpt => hallOpt.map(hall => updateTable(hall)(originalOrRefToUpdate(row, column, None)))
  def addRefereeToHall(row: Int, column: Int, referee: Player): (Option[Hall]) => Option[Hall] = hallOpt => hallOpt.map(hall => updateTable(hall)(originalOrRefToUpdate(row, column, Some(referee))))

  def deleteHallRef(hallId: String, row: Int, column: Int, referee: Player): Future[Option[Hall]] = {
    hallRepository.findById(hallId).map { hallOpt =>
      val newHallOpt = deleteRefereeOn(row, column, referee)(hallOpt)
      newHallOpt.map(newHall => hallRepository.update(newHall))
      newHallOpt
    }
  }


def deleteHallMatch (hall: Hall, row: Int, column: Int): Hall = updateTable (hall) (originalOrMatchToUpdate (row, column, None) )

def deleteMatchInHall (hallId: String, row: Int, column: Int): Future[Option[Hall]] = {
  hallRepository.findById (hallId).flatMap {
  case Some (hall) =>
  val updatedHall = deleteHallMatch (hall, row, column)
  hallRepository.update (updatedHall).map (Some (_) )
  case None => Future (None)
}

}

  def deleteMatchInHall(hallId: String, matchId: String): Future[Option[Hall]] =  hallRepository.findById(hallId).flatMap {
    case Some(realHall) => update(updateTable(realHall)(table => if (table.pingpongMatch.exists(_.id == matchId)) table.copy(pingpongMatch = None) else table)).map(Some(_))
    case _ => Future(None)
  }


  def setMatchToTable (hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch): Future[Option[Hall]] = {
  hallRepository.findFirstByField("id", hallId).map {
  case Some (hall) => Some (updateTable (hall) (originalOrMatchToUpdate (row, column, Some (pingpongMatch) ) ) )
  case _ => None
}.flatMap {
  case Some (updatedHall) =>
  hallRepository.update (updatedHall).map (hall => Some (hall) )
  case _ => Future (None)
}
}

  protected def updateTable (hall: Hall) (updateFunc: (HallTable) => HallTable): Hall = hall.copy (tables = hall.tables.map (updateFunc) )

  protected def originalOrMatchToUpdate (row: Int, column: Int, pingpongMatch: Option[PingpongMatch] ): (HallTable) => HallTable =
  hallTable => if (hallTable.row == row && hallTable.column == column) hallTable.copy (pingpongMatch = pingpongMatch) else hallTable

  protected def originalOrRefToUpdate (row: Int, column: Int, referee: Option[Player] ): (HallTable) => HallTable =
  hallTable => if (hallTable.row == row && hallTable.column == column) hallTable.copy (referee = referee) else hallTable


  def delete (hallId: String) = hallRepository.delete (hallId)

  def retrieveById (hallId: String) = hallRepository.findById(hallId)

  def update (hall: Hall): Future[Hall] = hallRepository.update (hall)

  def createIfNotExists (hall: Hall, isGreen: Boolean): Future[Either[String, Hall]] = hallRepository.findFirstByField("hallName", hall.hallName).flatMap {
  case Some (foundHall) => Future (Left (s"De opgegeven zaalnaam ${
  hall.hallName
} bestaat al.") )
  case None => hallRepository.create (hall.copy (tables = generateTables (hall.id, hall.rows, hall.tablesPerRow, isGreen) ) ).map (Right (_) )
}


  def retrieveAll: Future[List[Hall]] = hallRepository.findAll ()


  def generateTables (hallId: String, rows: Int, columns: Int, isGreen: Boolean): List[HallTable] = {
  for {
  row <- 1 to rows
  col <- 1 to columns
} yield HallTable (hallId, row, col, None, None, isGreen = isGreen)
}.toList
}
