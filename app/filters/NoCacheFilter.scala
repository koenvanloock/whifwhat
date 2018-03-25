package filters

import javax.inject.Inject

import akka.stream.Materializer
import play.api.mvc.{Result, RequestHeader, Filter}
import play.api.http.HeaderNames._
import scala.concurrent.{ExecutionContext, Future}

class NoCacheFilter @Inject()(
                               implicit override val mat: Materializer,
                               exec: ExecutionContext) extends Filter {

  override def apply(nextFilter: RequestHeader => Future[Result])
                    (requestHeader: RequestHeader): Future[Result] = {
    nextFilter(requestHeader).map { result =>
      if(requestHeader.method.equals("GET")) result.withHeaders(
        PRAGMA -> "no-cache",
        CACHE_CONTROL -> "no-cache,no-store, must-revalidate",
        ACCESS_CONTROL_ALLOW_ORIGIN -> "http://localhost:8080"
      ) else result
    }
  }

}
