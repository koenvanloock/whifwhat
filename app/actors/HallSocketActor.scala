package actors

import actors.ActiveHallActor.SetActiveHall
import akka.actor._
import models.halls.Hall
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global


object HallSocketActor {
  implicit val timeout = Timeout(5 seconds)
  def props(out: ActorRef, activeHallActorRef: ActorRef) = Props(new HallSocketActor(out, activeHallActorRef))
  case class Connect(clientId: String)
  case class Disconnect(clientId: String)
}

class HallSocketActor(out: ActorRef, activeHallActorRef: ActorRef) extends Actor {
  import HallSocketActor._

  private var clients: List[String] = Nil


  def receive: PartialFunction[Any, Unit] = {
    case Connect(id: String) => id :: clients
    case Disconnect(id: String) => clients = clients.filter(_ != id)
    case msg: Hall =>
      (activeHallActorRef ? SetActiveHall(msg)).mapTo[Option[Hall]].map{
        case Some(hall) =>
          println("Socket: " + hall)
          out ! hall
        case _ => println("no active hall")
      }
  }
}
