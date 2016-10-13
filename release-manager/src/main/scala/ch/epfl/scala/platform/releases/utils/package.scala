package ch.epfl.scala.platform.releases

import scala.util.Try
import scala.util.control.NonFatal

package object utils {
  type ReleaseResult[T] = Either[Error, T]

  implicit class TryWrapper[T](t: Try[T]) {
    def toReleaseResult(errorFeedback: String): ReleaseResult[T] = {
      t.map(Right.apply).recover {
        case NonFatal(e) => Left(Error(errorFeedback, Some(e)))
      }.get
    }
  }
}
