package actors

import actors.ActiveHall.{GetHall, _}
import actors.StreamActor.PublishActiveHall
import actors.TournamentEventActor.FreedHallPlayers
import akka.actor.{Actor, ActorRef, Props}
import models.halls.{Hall, HallTable}
import models.matches.PingpongMatch
import models.player.Player
import utils.PingpongMatchUtils._


object ActiveHall {
  def props: Props = Props(new ActiveHall())

  case class SetChannel(channelRef: ActorRef)

  case class ActivateHall(newHall: Hall, sender: ActorRef)

  case object RemoveActiveHall

  case class GetHall(sender: ActorRef)

  case object GetHallPlayers

  case class MoveMatchToHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch)

  case class DeleteMatchInHall(hallId: String, row: Int, column: Int)

  case class UpdateMatchInHall(pingpongMatch: PingpongMatch)

  case class MoveRefereeToTable(hallId: String, row: Int, column: Int, player: Player)

  case class DeleteRefereeFromTable(hallId: String, row: Int, column: Int, player: Player)

  case class DeleteMatchById(id: String, completed: Boolean)

  case class GetReferee(matchId: String)
}


class ActiveHall extends Actor {

  var activeHall: Option[Hall] = None
  var channelOpt: Option[ActorRef] = None

  def getRefereeOfMatch(matchId: String): Option[Player] =
    activeHall
    .map(realHall => realHall.tables.find( table => table.pingpongMatch.exists(_.id == matchId)).flatMap(_.referee))
    .getOrElse(None)

  override def receive: Receive = {
    case SetChannel(channel) => channelOpt = Some(channel)
    case GetHall(originalSender) => originalSender ! activeHall
    case RemoveActiveHall => activeHall = None
    case ActivateHall(newHall, senderRef) => activeHall = Some(newHall); senderRef ! activeHall
    case MoveMatchToHall(hallId, row, column, pingpongMatch) => moveMatchInHall(hallId, row, column, pingpongMatch)
    case DeleteMatchInHall(hallId, row, column) => deleteMatchInHall(hallId, row, column)
    case UpdateMatchInHall(pingpongMatch) => updateMatchInHall(pingpongMatch)
    case DeleteMatchById(matchId, completed) => deleteMatchInHall(matchId, completed)
    case MoveRefereeToTable(hallId, row, column, player) => updateRef(hallId, row, column, Some(player))
    case DeleteRefereeFromTable(hallId, row, column, player) => updateRef(hallId, row, column, None)
    case GetHallPlayers => sender() ! getHallPlayers
    case GetReferee(matchId) => sender() ! getRefereeOfMatch(matchId)
    case _ => sender() ! "method not supported!"
  }

  private def getHallPlayers = this.activeHall.toList.flatMap(trueHall => trueHall.tables.flatMap(table => table.pingpongMatch.toList.flatMap(getMatchPlayers)))

  private def deleteMatchInHall(matchId: String, completed: Boolean) = {
    withHall(deleteById(matchId, completed))
  }

  private def deleteMatchInHall(hallId: String, row: Int, column: Int) =
    withExistingHall(hallId)(updateTableMatchInhall(None, row, column))


  private def moveMatchInHall(hallId: String, row: Int, column: Int, pingpongMatch: PingpongMatch) =
    withExistingHall(hallId)(updateTableMatchInhall(Some(pingpongMatch), row, column))


  private def updateRef(hallId: String, row: Int, column: Int, somePlayer: Option[Player]) =
    withExistingHall(hallId)(updateTableRefInhall(somePlayer, row, column))


  private def updateTableRefInhall(refOpt: Option[Player], row: Int, column: Int): Hall => Hall = realHall => {
    realHall.copy(tables = performTableMutationAt(row, column, table => table.copy(referee = refOpt))(realHall.tables))
  }

  private def updateMatchInHall(pingpongMatch: PingpongMatch): Unit ={
    withHall( hall => hall.tables.find( hallTable => hallTable.pingpongMatch.exists(_.id == pingpongMatch.id)).map{
      table => updateTableMatchInhall(Some(pingpongMatch), table.row, table.column)(hall)
    }.getOrElse(hall))
  }

  private def updateTableMatchInhall(pingpongMatch: Option[PingpongMatch], row: Int, column: Int): Hall => Hall = realHall => {
    if(realHall.tables.find(table => table.row == row && table.column == column).exists(table => table.pingpongMatch.nonEmpty)){
      realHall.tables.find(table => table.row == row && table.column == column)
        .foreach(table => table.pingpongMatch
          .foreach(realMatch => sender !  FreedHallPlayers(getMatchPlayers(realMatch)))
        )
    }
    realHall.copy(tables = performTableMutationAt(row, column, table => table.copy(pingpongMatch = pingpongMatch))(realHall.tables))
  }

  private def deleteById(matchId: String, completed: Boolean): Hall => Hall = hall => {hall.copy(tables = hall.tables.map(table => if (table.pingpongMatch.exists(_.id == matchId)) {
    if(completed) table.referee.foreach{ realRef => sender() ! TournamentEventActor.HallRefereeDelete(hall.id, table.row, table.column, realRef, completed = true)}
    table.copy(pingpongMatch = None)
  } else table))
  }

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
