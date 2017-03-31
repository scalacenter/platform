package ch.epfl.scala.platform

import coursier.core.Version
import coursier.core.Version.{Literal, Qualifier}

import scala.util.Try

trait VersionUtils {
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
