package ch.epfl.scala.platform.releases

import java.io.File

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import stoml.TomlParserApi._

class ModulesReaderSuite extends JUnitSuite {
  val filepath = "/MODULES.toml"
  val resourceFile = new File(getClass.getResource(filepath).toURI)

  @Test def readModulesFile(): Unit = {
    val tomlContent = parseToml(resourceFile).get.value
    assert(tomlContent.lookup(Vector("modules", "module1")).isDefined)
    assert(tomlContent.lookup(Vector("modules", "module2")).isDefined)
  }
}
