import javax.inject.Singleton

import play.api.libs.concurrent.AkkaGuiceSupport
import actors.{StreamActor, TournamentEventActor}
import com.google.inject.AbstractModule

@Singleton
class Actors extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[TournamentEventActor]("tournament-event-actor")
    bindActor[StreamActor]("stream-actor")
  }
}