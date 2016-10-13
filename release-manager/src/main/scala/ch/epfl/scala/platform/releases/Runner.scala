package ch.epfl.scala.platform.releases

import java.nio.file.Paths

import scala.util.Try
import scala.util.control.NonFatal

object Runner {
  def main(args: Array[String]): Unit = {
    if (args.length == 1) {
      Try {
        val modulesFile = Paths.get(args(0))
      }
    }
  }
}
