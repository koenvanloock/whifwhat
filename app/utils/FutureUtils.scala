package utils

import scala.concurrent.{Future, Promise, ExecutionContext}
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

object FutureUtils {

  implicit class FutureOps[Z](fZ: Future[Z]) {

    def seq[Y](fY: Future[Y]): Future[Y] = fZ.flatMap { _ =>
      fY
    }

    def and[Y](fY: Future[Y]): Future[(Z, Y)] =
      fZ.flatMap { z =>
        fY.map { y =>
          (z, y)
        }
      }

    def always[Y](fY: Future[Y]): Future[Y] =
      fZ.flatMap { _ =>
        fY
      }.recoverWith {
        case _ =>
          fY
      }

  }

  // defining, once and for all,
  // how to try to execute code asynchronously
  //
  // this is done by making use of a promise
  // that is both (kind of) a try and (kind of) a future
  //
  // o a `promise` can be completed with a try using `promise.complete(_)`
  // o a `promise` can be exposed as a future using `promise.future`
  //
  def async[Z](code: => Z)(implicit executionContext: ExecutionContext): Future[Z] = {
    import TryUtils._
    val promise = Promise[Z]()
    // `promise` is "fulfilled" in a separate `executionContext` by
    //   o trying to execute `code`
    //   o binding the resulting bindable to an executor completing `promise`
    executionContext.prepare().execute(new Runnable {
      def run {
        tryExec(code).bind(promise.complete(_))
      }
    })
    promise.future
  }

  implicit class FutureObjectOps(fo: Future.type) {

    def futureUnit[Z](z: Z) = Future(z)

    def optionSequence[Z](ofz: Option[Future[Z]]): Future[Option[Z]] =
      ofz match {
        case Some(fz) => fz.map(Some(_))
        case None => Future(None)
      }

    def flatOptionSequence[Z](ofoz: Option[Future[Option[Z]]]): Future[Option[Z]] =
      optionSequence(ofoz).map(_.flatten)

    def flatListSequence[Z](lflz: List[Future[List[Z]]]): Future[List[Z]] =
      Future.sequence(lflz).map(_.flatten)

  }

  implicit class FutureOptionOps[Z](foz: Future[Option[Z]]) {
    def futureOptionFlatMap[Y](z2foy: Z => Future[Option[Y]]): Future[Option[Y]] =
      foz.flatMap { oz =>
        Future.optionSequence(oz.map(z2foy)).map(_.flatten)
      }

    def traverse[Y](z2fy: Z => Future[Y]): Future[Option[Y]] =
      foz.flatMap { oz =>
        Future.optionSequence(oz.map(z2fy))
      }
  }

  implicit class FutureListOps[Z](flz: Future[List[Z]]) {
    def futureListFlatMap[Y](z2fly: Z => Future[List[Y]]): Future[List[Y]] =
      flz.flatMap { lz =>
        Future.sequence(lz.map(z2fly)).map(_.flatten)
      }

    def futureListMap[Y](z2ly: Z => Y): Future[List[Y]] =
      futureListFlatMap[Y] { z =>
        Future(List(z2ly(z)))
      }

    def futureListFilter(z2b: Z => Boolean): Future[List[Z]] =
      flz.futureListFlatMap { z =>
        if (z2b(z)) Future(List(z))
        else Future(Nil)
      }

    def traverse[Y](z2fy: Z => Future[Y]): Future[List[Y]] =
      flz.flatMap { lz =>
        Future.sequence(lz.map(z2fy))
      }
  }

}

object AnyUtils {

  implicit class AnyOps[Z](z: Z) {
    def mkStringWithTag(tag: String)(prefix: String, suffix: String) = {
      s"$prefix$tag: $z$suffix"
    }
  }

}


  trait Bindable[Z, Y] {
    val bind: (Z => Y) => Y
  }

object TryUtils {

  import AnyUtils._

  // trying to `exec` `code`
  //
  def tryExec[Z](code: => Z) = exec(Try(code))
  def exec[Z](code: => Z): Bindable[Z, Unit] = new Bindable[Z, Unit] {
    val bind: (Z => Unit) => Unit =
      executor => executor.apply(code)
  }
}

