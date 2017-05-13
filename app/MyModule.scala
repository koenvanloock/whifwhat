import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

import actors.TournamentEventActor

class MyModule extends AbstractModule with AkkaGuiceSupport {
  def configure = {
    bindActor[TournamentEventActor]("tournament-event-actor")
  }
}