package actors

import actors.ActiveHall._
import actors.StreamActor.PublishActiveHall
import akka.actor.{Actor, ActorRef, Props}
import models.halls.{Hall, HallTable}
import models.matches.PingpongMatch
import models.player.Player

object ActiveHall {
  def props: Props = Props(new ActiveHall())

  case class SetChannel(channelRef: ActorRef)

  case class ActivateHall(newHall: Hall, sender: ActorRef)

  case object RemoveActiveHall

  case class GetHall(sender: ActorRef)

  case class MoveMatchToHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)

  case class DeleteMatchInHall(hallId: String, row: Int, column: Int)

  case class MoveRefereeToTable(hallId: String, row: Int, column: Int, player: Player)

  case class DeleteRefereeFromTable(hallId: String, row: Int, column: Int, player: Player)

  case class DeleteMatchById(id: String)

}


class ActiveHall extends Actor {

  var activeHall: Option[Hall] = None
  var channelOpt: Option[ActorRef] = None

  override def receive: Receive = {
    case SetChannel(channel) => channelOpt = Some(channel)
    case GetHall(originalSender) => originalSender ! activeHall
    case RemoveActiveHall => activeHall = None
    case ActivateHall(newHall, senderRef) => activeHall = Some(newHall); senderRef ! activeHall
    case MoveMatchToHall(hallId, row, column, pingpongMatch) => activateHall(hallId, row, column, pingpongMatch)
    case DeleteMatchInHall(hallId, row, column) => deleteMatchInHall(hallId, row, column)
    case DeleteMatchById(matchId) => deleteMatchInHall(matchId)
    case MoveRefereeToTable(hallId, row, column, player) => updateRef(hallId, row, column, Some(player))
    case DeleteRefereeFromTable(hallId, row, column, player) => updateRef(hallId, row, column, None)
    case _ => "method not supported!"
  }

  private def deleteMatchInHall(matchId: String) = {
    withHall(deleteById(matchId))
  }

  private def deleteMatchInHall(hallId: String, row: Int, column: Int) =
    withExistingHall(hallId)(updateTableMatchInhall(None, row, column))


  private def activateHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch) =
    withExistingHall(hallId)(updateTableMatchInhall(Some(pingpongMatch), row, column))


  private def updateRef(hallId: String, row: Int, column: Int, somePlayer: Option[Player]) =
    withExistingHall(hallId)(updateTableRefInhall(somePlayer, row, column))


  private def updateTableRefInhall(refOpt: Option[Player], row: Int, column: Int): Hall => Hall = realHall => {
    realHall.copy(tables = performTableMutationAt(row, column, table => table.copy(referee = refOpt))(realHall.tables))
  }

  private def updateTableMatchInhall(pingpongMatch: Option[PingpongMatch], row: Int, column: Int): Hall => Hall = realHall => {
    realHall.copy(tables = performTableMutationAt(row, column, table => table.copy(pingpongMatch = pingpongMatch))(realHall.tables))
  }

  private def deleteById(matchId: String): Hall => Hall = hall => hall.copy(tables = hall.tables.map(table => if (table.pingpongMatch.exists(_.id == matchId)) table.copy(pingpongMatch = None) else table))

  private def withExistingHall(hallId: String)(hallMutation: Hall => Hall) = {
    activeHall.foreach { realHall =>
      if (hallId == realHall.id) {
        val newHall = hallMutation(realHall)
        activeHall = Some(newHall)
        channelOpt.foreach(channel => channel ! PublishActiveHall(newHall))
      }
    }
  }

  private def withHall(hallMutation: Hall => Hall) = {
    activeHall.foreach { realHall =>
      val newHall = hallMutation(realHall)
      activeHall = Some(newHall)
      channelOpt.foreach(channel => channel ! PublishActiveHall(newHall))
    }
  }

  private def performTableMutationAt(row: Int, column: Int, tableMuation: HallTable => HallTable): List[HallTable] => List[HallTable] =
    tables => tables.map(table => if (table.row == row && table.column == column) tableMuation(table) else table)
}
