package ch.epfl.scala.platform.util

import scala.language.implicitConversions

/** Represent a generic error in the release process. */
case class Error(msg: String, throwable: Option[Throwable] = None) {
  import Console.{RED_B, RESET}
  override def toString: String =
    s"$RED_B$msg$RESET\n${throwable.map(_.toString).getOrElse("")}"
}

