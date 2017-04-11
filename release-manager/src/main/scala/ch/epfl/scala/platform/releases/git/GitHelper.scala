package ch.epfl.scala.platform.releases.git

import java.nio.file.{Files, Path}

import ch.epfl.scala.platform.releases.{Feedback, Module}
import ch.epfl.scala.platform.releases.util._
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref

import scala.util.Try

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

  final class GitDirWrapper(repoDir: java.io.File) {
    def getTrackingRemote(repo: GitRepo): ReleaseResult[String] = {
      import scala.collection.JavaConversions._
      repo.right.flatMap { r =>
        val remotes: List[String] =
          r.getRepository.getRemoteNames.iterator().toList
        lazy val config = r.getRepository.getConfig
        val currentBranch = r.getRepository.getBranch
        val currentRemote = {
          if (remotes.size == 1) Some(remotes.head)
          else Option(config.getString("branch", currentBranch, "remote"))
        }
        currentRemote
          .flatMap(name => Option(config.getString("remote", name, "url")))
          .toReleaseResult(Feedback.missingRemote(currentBranch))
      }
    }
  }

  implicit class GitWrapper(repo: GitRepo) {
    def checkout(branch: String): ReleaseResult[(Ref, Git)] = {
      repo.right.flatMap { r =>
        Try(r.checkout().setName(branch).call() -> r)
          .toReleaseResult(Feedback.InvalidBranchCheckout)
      }
    }
  }
}
