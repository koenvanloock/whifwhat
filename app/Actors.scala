import javax.inject.Singleton

import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{ScoreActor, ScoreStreamer, StreamActor, TournamentEventActor}
import com.google.inject.AbstractModule

@Singleton
class Actors extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[TournamentEventActor]("tournament-event-actor")
    bindActor[StreamActor]("stream-actor")
    bindActor[ScoreStreamer]("score-streamer")
    bindActor[ScoreActor]("score-actor")
  }
}