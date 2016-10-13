package ch.epfl.scala.platform.releases

import org.junit.Test
import org.scalatest.junit.JUnitSuite

class ReleasePipelineSuite extends JUnitSuite {
  val fake = Module("fake", "https://github.com/scalacent/fake")
  val scaladex = Module("scaladex", "https://github.com/scalacenter/scaladex")
  val fakePipeline = new ReleaseManager(fake)
  val scaladexPipeline = new ReleaseManager(scaladex)

  @Test def cloneToCorrectRepo(): Unit = assert(scaladexPipeline.repo.isRight)
  @Test def cloneToIncorrectRepo(): Unit = assert(fakePipeline.repo.isLeft)

  @Test def checkoutCorrectBranch(): Unit =
    assert(scaladexPipeline.checkout("HEAD").isRight)
  @Test def checkoutIncorrectBranch(): Unit =
    assert(scaladexPipeline.checkout("unexisting").isLeft)

  @Test def release(): Unit =
    assert(scaladexPipeline.release.isRight)
}
