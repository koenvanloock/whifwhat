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
import play.api.libs.iteratee.{Concurrent, Enumerator}
import play.api.libs.streams.Streams

import scala.concurrent.ExecutionContext.Implicits.global

class StreamController @Inject()(@Named("score-streamer") scoreStreamer: ActorRef, @Named("stream-actor") streamActor: ActorRef, @Named("tournament-event-actor") tournamentEventActor: ActorRef) extends Controller {
  implicit val timeout = Timeout(5 seconds)

  val (out, channel) = Concurrent.broadcast[JsObject]
  streamActor ! Start(channel)

  val (scoreOut, scoreChanel) = Concurrent.broadcast[JsObject]
  scoreStreamer ! Start(scoreChanel)

  tournamentEventActor ! SetChannel(streamActor)

  def tournamentEventStream = Action { implicit req => enumeratorToStream(out)}

  def scoreEventStream = Action { implicit req => enumeratorToStream(scoreOut)}

  private def enumeratorToStream(enumerator: Enumerator[JsObject]) = {
    val source: Source[String, Any] = Source.fromPublisher(Streams.enumeratorToPublisher(enumerator.map(x => x.toString())))
    Ok.chunked(source via EventSource.flow).as("text/event-stream")
  }
}
