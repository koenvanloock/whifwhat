import javax.inject.Singleton

import actors.HallEventStreamActor.Start
import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{HallEventStreamActor, StreamActor, TournamentEventActor}
import com.google.inject.AbstractModule
import models.halls.Hall
import play.api.libs.iteratee.Concurrent

@Singleton
class Actors extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[TournamentEventActor]("tournament-event-actor")
    bindActor[StreamActor]("stream-actor")
  }
}