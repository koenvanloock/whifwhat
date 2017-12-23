package controllers

import javax.inject.{Inject, Named}

import actors.ScoreStreamer
import actors.StreamActor.Start
import actors.TournamentEventActor.SetChannel
import akka.actor.ActorRef
import akka.util.Timeout
import play.api.libs.json.JsObject
import play.api.mvc.{InjectedController, Result}

import scala.concurrent.duration._
import akka.stream.scaladsl.Source
import play.api.libs.EventSource
import play.api.libs.iteratee.streams.IterateeStreams
import play.api.libs.iteratee.{Concurrent, Enumerator}

import scala.concurrent.ExecutionContext.Implicits.global

class StreamController @Inject()(@Named("score-streamer") scoreStreamer: ActorRef, @Named("stream-actor") streamActor: ActorRef, @Named("tournament-event-actor") tournamentEventActor: ActorRef) extends InjectedController {
  implicit val timeout = Timeout(5 seconds)

  val (out, channel) = Concurrent.broadcast[JsObject]
  streamActor ! Start(channel)

  val (scoreOut, scoreChanel) = Concurrent.broadcast[JsObject]
  scoreStreamer ! ScoreStreamer.Start(scoreChanel)

  tournamentEventActor ! SetChannel(streamActor)

  def tournamentEventStream = Action {  enumeratorToStream(out)}

  def scoreEventStream = Action { enumeratorToStream(scoreOut)}

  private def enumeratorToStream(enumerator: Enumerator[JsObject]): Result = {
    val source: Source[String, Any] = Source.fromPublisher(IterateeStreams.enumeratorToPublisher(enumerator.map(x => x.toString())))
    Ok.chunked(source via EventSource.flow).as("text/event-stream")
  }
}
