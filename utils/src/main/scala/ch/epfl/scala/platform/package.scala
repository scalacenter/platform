package ch.epfl.scala

package object platform {
  object logger {
    def elem[T](es: sourcecode.Text[T]*)(implicit line: sourcecode.Line,
                                         file: sourcecode.File): Unit = {
      // TODO: Change and integrate with SBT reporter
      if (System.getProperty("platform.debug") == "true") {
        es.foreach { e =>
          val filename = file.value.replaceAll(".*/", "")
          println(s"$filename:${line.value} ${e.value}")
        }
      }
    }
  }
}
