package ch.epfl.scala.platform.releases.utils

case class Error(msg: String, throwable: Option[Throwable] = None)