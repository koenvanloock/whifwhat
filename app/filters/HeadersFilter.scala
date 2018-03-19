package filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.http.HeaderNames._
import play.api.mvc.{Filter, RequestHeader, Result}

import scala.concurrent.{ExecutionContext, Future}

class HeadersFilter @Inject()(
                               implicit override val mat: Materializer,
                               exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    nextFilter(requestHeader).map { result =>
      result.withHeaders(
        PRAGMA -> "no-cache",
        CACHE_CONTROL -> "no-cache,no-store, must-revalidate",
        ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
        ACCESS_CONTROL_ALLOW_HEADERS -> "*"
      )
    }
  }

}
