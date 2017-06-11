package actors

import akka.actor.{Actor, Props}
import models.halls.Hall
import play.api.libs.iteratee.Concurrent

object HallEventStreamActor{
  def props = Props[HallEventStreamActor]

  case class Start(out: Concurrent.Channel[Hall])
  case class ActivateHall(hall: Hall)
}

class HallEventStreamActor  extends Actor {
  import HallEventStreamActor._
    var out: Option[Concurrent.Channel[Hall]] = None
    def receive = {
      case Start(outChannel)       => this.out = Some(outChannel)
      case ActivateHall(hall) =>
        //println("printing hall to stream: " + hall.hallName)
        this.out.foreach(_.push(hall))
    }
}
