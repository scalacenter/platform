package ch.epfl.scala.platform.github

import java.io.File

trait GitHubResources {
  trait Resource {
    val contentType: String
    val name: String
    val label: String
    val file: File
  }

  case class Jar(name: String, label: String, filepath: String)
      extends Resource {
    val contentType = "application/zip"
    val file = java.nio.file.Paths.get(filepath).toFile
  }
}
