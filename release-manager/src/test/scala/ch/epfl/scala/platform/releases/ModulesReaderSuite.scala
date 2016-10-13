package ch.epfl.scala.platform.releases

import java.nio.file._

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import stoml.TomlParserApi._
import scala.collection.JavaConverters._

class ModulesReaderSuite extends JUnitSuite {
  val filepath = "/MODULES.toml"
  val resourcePath = Paths.get(getClass.getResource(filepath).toURI)
  val contents = Files.readAllLines(resourcePath).asScala.mkString("\n")

  @Test def readModulesFile(): Unit = {
    val tomlContent = toToml(contents).get.value
    assert(tomlContent.lookup(Vector("modules", "module1")).isDefined)
    assert(tomlContent.lookup(Vector("modules", "module2")).isDefined)
  }
}
