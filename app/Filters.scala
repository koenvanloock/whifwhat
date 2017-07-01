import javax.inject._
import play.api._
import play.api.http.HttpFilters
import play.api.mvc._

import filters.NoCacheFilter


@Singleton
class Filters @Inject() (
  env: Environment,
  nocache: NoCacheFilter) extends HttpFilters {

  override val filters = {
    if (env.mode == Mode.Dev) Seq(nocache) else Seq.empty
  }

}
