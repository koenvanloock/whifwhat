package helpers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable}

/**
  * @author Koen Van Loock
  * @version 1.0 23/04/2016 18:25
  */
object TestHelpers {

  val DEFAULT_DURATION = Duration(3000, "millis")
  def waitFor[T](block: Awaitable[T]): T = Await.result(block, DEFAULT_DURATION)


}
