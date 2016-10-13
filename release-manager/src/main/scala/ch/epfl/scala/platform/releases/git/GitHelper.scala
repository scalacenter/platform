package ch.epfl.scala.platform.releases.git

import java.nio.file.{Files, Path, Paths}

import ch.epfl.scala.platform.releases.{Feedback, Module}
import ch.epfl.scala.platform.releases.utils._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Ref

import scala.util.Try
import scala.util.control.NonFatal

trait GitHelper {
  type GitRepo = ReleaseResult[Git]

  private[platform] val clonedFolder = "release-manager"
  val targetDir: ReleaseResult[Path] = {
    Try(Files.createTempDirectory(clonedFolder))
      .toReleaseResult(Feedback.FailedTargetDirCreation)
  }

  def clone(module: Module): ReleaseResult[Git] = {
    targetDir.right.flatMap { tmpParent =>
      val maybeGit = for {
        dir <- Try(Files.createTempDirectory(tmpParent, module.name).toFile)
        action = Git.cloneRepository().setURI(module.repo).setDirectory(dir)
        cloned <- Try(action.call)
      } yield Right[Error, Git](cloned)
      maybeGit.recover {
        case t: Throwable =>
          Left(Error(Feedback.FailedRepoCreation, Some(t)))
      }.get
    }
  }

  implicit class GitWrapper(repo: GitRepo) {
    def checkout(branch: String = "master"): ReleaseResult[(Ref, Git)] = {
      repo.right.flatMap { r =>
        Try(r.checkout().setName(branch).call() -> r)
          .toReleaseResult(Feedback.InvalidBranchCheckout)
      }
    }
  }
}
