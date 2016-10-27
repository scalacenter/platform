package ch.epfl.scala.platform.search

import org.junit.Test
import org.scalatest.junit.JUnitSuite

class ModuleSearchSpec extends JUnitSuite {
  @Test def search(): Unit = {
    // Always works, organization id is not used anymore
    val module = ScalaModule("com.github.jvican", "stoml", "2.11")
    val search = ModuleSearch.searchInMaven(module)
    search.leftMap(e => throw e.throwable.get)
    assert(search.getOrElse(List()).size == 1)
  }

  @Test def fetchLatestCorrectVersion(): Unit = {
    def emptyResolvedModule(version: String) =
      ResolvedModule("", "", "", None, Nil, Nil, version)
    val winner = emptyResolvedModule("0.2.2")
    val targets = List(
      emptyResolvedModule("0.1.0"),
      emptyResolvedModule("0.2.1"),
      emptyResolvedModule("0.2.1-BETA"),
      emptyResolvedModule("0.2.1-2010-08-10"),
      winner,
      emptyResolvedModule("0.2.2-beta"),
      emptyResolvedModule("0.2.2-alpha")
    )
    val presumedWinner = ModuleSearch.compareAndGetLatest(targets)
    assert(presumedWinner.isDefined)
    assert(presumedWinner.get == winner)
  }
}
