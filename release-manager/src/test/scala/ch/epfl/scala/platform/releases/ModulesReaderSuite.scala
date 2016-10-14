package ch.epfl.scala.platform.releases

import java.io.File

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import stoml.TomlParserApi._

class ModulesReaderSuite extends JUnitSuite {
  val filepath = "/MODULES.toml"
  val resourceFilepath = new File(getClass.getResource(filepath).toURI).getAbsolutePath

  @Test def readModulesFile(): Unit = {
    val modules = ModulesReader.read(resourceFilepath)
    assert(modules.isRight)
    assert(modules.right.exists(_.length == 2))
  }
}
