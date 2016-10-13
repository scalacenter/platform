package ch.epfl.scala.platform

import scala.util.Properties

package object platform {
  object logger {
    def elem[T](es: sourcecode.Text[T]*)(implicit line: sourcecode.Line,
                                         file: sourcecode.File): Unit = {
      // TODO: Change and integrate with SBT reporter
      if (Properties.propIsSet("platform.release.debug")) {
        es.foreach { e =>
          val filename = file.value.replaceAll(".*/", "")
          println(s"$filename:${line.value} [${e.source}] ${e.value}")
        }
      }
    }
  }
}
