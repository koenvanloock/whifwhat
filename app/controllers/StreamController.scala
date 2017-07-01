package controllers

import javax.inject.{Inject, Named}

import actors.StreamActor.Start
import actors.TournamentEventActor.SetChannel
import akka.actor.ActorRef
import akka.util.Timeout
import play.api.libs.json.JsObject
import play.api.mvc.{Action, Controller}

import scala.concurrent.duration._
import akka.stream.scaladsl.Source
import play.api.libs.EventSource
import play.api.libs.iteratee.Concurrent
import play.api.libs.streams.Streams

import scala.concurrent.ExecutionContext.Implicits.global

class StreamController @Inject()(@Named("stream-actor") streamActor: ActorRef, @Named("tournament-event-actor") tournamentEventActor: ActorRef) extends Controller {
  implicit val timeout = Timeout(5 seconds)

  val (out, channel) = Concurrent.broadcast[JsObject]
  streamActor ! Start(channel)

  tournamentEventActor ! SetChannel(streamActor)

  def eventStream = Action { implicit req =>
    val source: Source[String, Any] = Source.fromPublisher(Streams.enumeratorToPublisher(out.map(x => x.toString())))

    Ok.chunked(source via EventSource.flow).as("text/event-stream")
  }
}
