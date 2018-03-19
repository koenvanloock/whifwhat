import javax.inject._
import play.api._
import play.api.http.HttpFilters
import play.api.mvc._

import filters.HeadersFilter


@Singleton
class Filters @Inject() (
  env: Environment,
  nocache: HeadersFilter) extends HttpFilters {

  override val filters = {
    if (env.mode == Mode.Dev) Seq(nocache) else Seq.empty
  }

}
