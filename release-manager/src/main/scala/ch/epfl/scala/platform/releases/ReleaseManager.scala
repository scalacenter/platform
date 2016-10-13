package ch.epfl.scala.platform.releases

import java.nio.file.Files

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Ref

import scala.util.Either.RightProjection
import scala.util.control.NonFatal
import scala.util.Try

class ReleaseManager(module: Module) {

  case class Error(msg: String, throwable: Option[Throwable] = None)

  type ReleaseResult[T] = Either[Error, T]

  private def cloneRepo: ReleaseResult[Git] = {
    val maybeGit = for {
      dir <- Try(Files.createTempDirectory(module.name).toFile)
      action = Git.cloneRepository().setURI(module.repo).setDirectory(dir)
      cloned <- Try(action.call)
    } yield Right[Error, Git](cloned)
    maybeGit.recover {
      case t: Throwable =>
        Left(Error(Feedback.FailedRepoCreation, Some(t)))
    }.get
  }

  lazy val repo: ReleaseResult[Git] = cloneRepo

  def checkout(branch: String = "platform"): ReleaseResult[Ref] = {
    val done = Try(repo.right.map(_.checkout().setName(branch).call()))
    done.recover {
      case ge: GitAPIException =>
        Left(Error(Feedback.InvalidBranchCheckout, Some(ge)))
    }.get
  }

  def release: ReleaseResult[String] = {
    import sys.process._
    repo.right.map { r =>
      val dir = r.getRepository.getDirectory
      Process(Seq("sbt", "test"), dir).!!
    }
  }
}
