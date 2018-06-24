package repositories.numongo.repos

import com.google.inject.Inject
import models.{SeriesFormat, TournamentSeries}
import play.modules.reactivemongo.ReactiveMongoApi
import repositories.numongo.GenericMongoRepository

class SeriesRepository @Inject()(implicit val reactiveMongoApi: ReactiveMongoApi)
  extends GenericMongoRepository[TournamentSeries, String]("series", SeriesFormat.reads, SeriesFormat.writes)(reactiveMongoApi)