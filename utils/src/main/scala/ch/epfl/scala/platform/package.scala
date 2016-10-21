package ch.epfl.scala

package object platform {
  type Version = coursier.core.Version
  object logger {
    def elem[T](es: sourcecode.Text[T]*)(implicit line: sourcecode.Line,
                                         file: sourcecode.File): Unit = {
      // TODO: Change and integrate with SBT reporter
      if (System.getProperty("platform.debug") == "true") {
        es.foreach { e =>
          val filename = file.value.replaceAll(".*/", "")
          println(s"$filename:${line.value} [${e.source}] ${e.value}")
        }
      }
    }
  }
}
