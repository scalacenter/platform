package ch.epfl.scala.platform.releases

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import ch.epfl.scala.platform.releases.git._

class ReleasePipelineSuite extends JUnitSuite {
  val fake = Module("fake", "https://github.com/scalacent/fake")
  val dummy = Module("dummy", "https://github.com/scalaplatform/dummy")
  lazy val dummyRepo = git.clone(dummy)
  lazy val dummyManager = ReleaseManager(dummy, "HEAD")

  @Test def cloneToCorrectRepo(): Unit =
    assert(dummyRepo.isRight)
  @Test def cloneToIncorrectRepo(): Unit =
    assert(git.clone(fake).isLeft)

  @Test def checkoutCorrectBranch(): Unit =
    assert(dummyRepo.checkout("HEAD").isRight)
  @Test def checkoutIncorrectBranch(): Unit =
    assert(dummyRepo.checkout("unexisting").isLeft)

  @Test def release(): Unit =
    assert(dummyManager.release.isRight)
}
