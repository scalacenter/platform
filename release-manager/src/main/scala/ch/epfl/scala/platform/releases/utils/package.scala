package ch.epfl.scala.platform.releases

package object utils {
  type ReleaseResult[T] = Either[Error, T]
}
