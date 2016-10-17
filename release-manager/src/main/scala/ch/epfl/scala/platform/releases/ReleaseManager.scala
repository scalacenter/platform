package ch.epfl.scala.platform.releases

import ch.epfl.scala.platform.releases.git._
import ch.epfl.scala.platform.releases.utils._

import scala.util.Try

trait ReleasePipeline
case object Nightly extends ReleasePipeline
case object Beta extends ReleasePipeline
case object Stable extends ReleasePipeline

/** Manage the release of a module relying on the sbt-release plugin. */
case class ReleaseManager(module: Module, branch: String = "platform-release") {
  lazy val repo = git.clone(module).checkout(branch)

  def releaseCmdTemplate(cmd: String): Seq[String] = s"sbt $cmd".split(" ")
  val nightlyReleaseCmd = releaseCmdTemplate("releaseNightly")
  val betaReleaseCmd = releaseCmdTemplate("releaseBeta")
  val stableReleaseCmd = releaseCmdTemplate("releaseStable")
  def release(pipeline: ReleasePipeline = Nightly): ReleaseResult[String] = {
    import sys.process._
    val releaseCmd = pipeline match {
      case Nightly => nightlyReleaseCmd
      case Beta => betaReleaseCmd
      case Stable => stableReleaseCmd
    }
    repo.right.flatMap { t =>
      val (_, g) = t
      val dir = g.getRepository.getDirectory.getParentFile
      Try(Process(releaseCmd, dir).!!)
        .toReleaseResult(Feedback.UnexpectedSbtReleaseError)
    }
  }

  def releaseEverything: ReleaseResult[String] = {
    for {
      releasedNightly <- release(Nightly).right
      releasedBeta <- release(Beta).right
      releasedStable <- release(Stable).right
    } yield s"$releasedNightly\n\n$releasedBeta\n\n$releasedStable"
  }
}
