package ch.epfl.scala.platform.releases

import ch.epfl.scala.platform.releases.git._
import ch.epfl.scala.platform.releases.utils._
import org.eclipse.jgit.lib.Ref

case class ReleaseManager(module: Module, branch: String = "platform-release") {
  lazy val repo = git.clone(module).checkout(branch)
  def release: ReleaseResult[String] = {
    import sys.process._
    repo.right.map { t =>
      val (_, g) = t
      val dir = g.getRepository.getDirectory
      Process(Seq("sbt", "test"), dir).!!
    }
  }
}
