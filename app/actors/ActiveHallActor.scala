package actors

import akka.actor.{Actor, Props}
import models.halls.Hall

object ActiveHallActor {
  def props = Props[ActiveHallActor]

    case class SetActiveHall(hall: Hall)
    case object ReleaseActiveHall
    case object GetHall
}

class ActiveHallActor extends Actor{
  import ActiveHallActor._

  private var activeHall: Option[Hall] = None

  override def receive: Receive = {
    case SetActiveHall(hall) =>
      activeHall = Some(hall)
      println(hall)
      sender() ! activeHall

    case GetHall => sender() ! activeHall
    case ReleaseActiveHall => activeHall = None
  }
}
