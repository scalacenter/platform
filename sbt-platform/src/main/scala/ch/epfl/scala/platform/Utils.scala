package ch.epfl.scala.platform

import ch.epfl.scala.platform.github.GitHubReleaser
import coursier.core.Version
import coursier.core.Version.{Literal, Qualifier}
import sbtrelease.Git

import scala.util.Try

trait Utils {
  def getRemoteUrl(g: Git): Option[(String, String)] = {
    def parseUrl(url: String): Option[(String, String)] = {
      url match {
        case GitHubReleaser.SshGitHubUrl(org, repo) => Some((org, repo))
        case GitHubReleaser.HttpsGitHubUrl(org, repo) => Some(org, repo)
        case _ => None
      }
    }

    if (g.hasUpstream) {
      // Upstream is set, get the upstream to which the current branch points to
      val trackingRemote = g.trackingRemote
      val p = g.cmd("config", "remote.%s.url" format trackingRemote)
      Try(p.!!.trim).toOption.flatMap(parseUrl)
    } else {
      // No upstream is set, get the first remote -> necessary for manual init
      Try(g.cmd("remote").!!.trim.lines).toOption.flatMap { remotes =>
        remotes.toList.headOption.flatMap { firstRemote =>
          Try(g.cmd("remote", "get-url", firstRemote).!!.trim).toOption
            .flatMap(parseUrl)
        }
      }
    }
  }

  implicit class XtensionCoursierVersion(v: Version) {
    def toSbtRelease: sbtrelease.Version = {
      val repr = v.repr
      sbtrelease
        .Version(repr)
        .getOrElse(sys.error(Feedback.unexpectedVersionInteraction(repr)))
    }
  }

  implicit class XtensionSbtReleaseVersion(v: sbtrelease.Version) {
    def toCoursier: Version = validateVersion(v.string)
  }

  def stripSnapshot(version: String): String =
    version.stripSuffix("-SNAPSHOT")

  def validateVersion(definedVersion: String): Version = {
    val validatedVersion = for {
      version <- Try(Version(definedVersion)).toOption
      // Double check that literals & qualifiers are stripped off
      if !version.items.exists(i =>
        i.isInstanceOf[Literal] || i.isInstanceOf[Qualifier])
    } yield version
    validatedVersion.getOrElse(
      sys.error(Feedback.invalidVersion(definedVersion)))
  }
}
