package ch.epfl.scala.platform

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import ch.epfl.scala.platform.search.{
  ModuleSearch,
  ResolvedModule,
  ScalaModule
}

class ModuleSearchSpec extends JUnitSuite {
  @Test def search(): Unit = {
    // Always works, organization id is not used anymore
    val module = ScalaModule("com.github.jvican", "stoml", "2.11")
    val search = ModuleSearch.searchInMaven(module)
    search.leftMap(e => throw new Exception(e))
    assert(search.getOrElse(List()).size == 1)
  }

  @Test def getCorrectLatestVersion(): Unit = {
    def emptyResolvedModule(version: String) =
      ResolvedModule("", "", "", None, Nil, Nil, version)
    val presumedWinner = emptyResolvedModule("0.2.2")
    val targets = List(
      emptyResolvedModule("0.1.0"),
      emptyResolvedModule("0.2.1"),
      emptyResolvedModule("0.2.1-BETA"),
      emptyResolvedModule("0.2.1-2010-08-10"),
      presumedWinner,
      emptyResolvedModule("0.2.2-beta"),
      emptyResolvedModule("0.2.2-alpha")
    )
    assert(ModuleSearch.compareAndGetLatest(targets) == presumedWinner)
  }
}
