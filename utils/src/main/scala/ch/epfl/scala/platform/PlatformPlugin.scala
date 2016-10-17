package ch.epfl.scala.platform

import com.typesafe.sbt.SbtPgp
import com.typesafe.sbt.pgp.PgpKeys
import sbt._

object PlatformPlugin extends sbt.AutoPlugin {
  override def trigger = allRequirements
  override def requires =
    bintray.BintrayPlugin &&
      sbtrelease.ReleasePlugin &&
      SbtPgp

  override def projectSettings = PlatformSettings.settings
  object autoImport extends DroneSettings {
    // FORMAT: OFF
    val platformModuleRepository = settingKey[Option[String]]("Git module repository.")
    val platformReleaseOnMerge = settingKey[Boolean]("Release on every PR merge.")
    platformReleaseOnMerge := false // By default, disabled
    val platformModuleTags = settingKey[Seq[String]]("Tags for the bintray module package.")
    platformModuleTags := Seq.empty[String]
    val platformTargetBranch = settingKey[String]("Branch used for the platform release.")
    platformTargetBranch := "platform-release"
    // FORMAT: ON
  }
}

trait DroneSettings {
  // Drone-defined environment variables
  val insideCi = settingKey[Boolean]("Checks if CI is executing the build.")
  val ciName = settingKey[Option[String]]("Get the name of the CI server.")
  val ciRepo = settingKey[Option[String]]("Get the repository run by the CI.")
  val ciBranch = settingKey[Option[String]]("Get the current git branch.")
  val ciCommit = settingKey[Option[String]]("Get the current git commit.")
  val ciBuildDir = settingKey[Option[String]]("Get the CI build directory.")
  val ciBuildUrl = settingKey[Option[String]]("Get the CI build URL.")
  val ciBuildNumber = settingKey[Option[Int]]("Get the CI build number.")
  val ciPullRequest = settingKey[Option[String]]("Get the pull request id.")
  val ciJobNumber = settingKey[Option[Int]]("Get the CI job number.")
  val ciTag = settingKey[Option[String]]("Get the git tag.")

  // Custom environment variables
  val sonatypeUsername = settingKey[Option[String]]("Get sonatype username.")
  val sonatypePassword = settingKey[Option[String]]("Get sonatype password.")
  val bintrayUsername = settingKey[Option[String]]("Get bintray username.")
  val bintrayPassword = settingKey[Option[String]]("Get bintray password.")
}

object PlatformSettings {

  def settings: Seq[Setting[_]] =
    resolverSettings ++ compilationSettings ++ bintraySettings

  import sbt._, Keys._
  import PlatformPlugin.autoImport._
  import sbtrelease.ReleasePlugin.autoImport._
  import ReleaseTransformations._
  import bintray.BintrayPlugin.autoImport._

  private val PlatformReleases =
    Resolver.bintrayRepo("scalaplatform", "modules-releases")
  private val PlatformTools =
    Resolver.bintrayRepo("scalaplatform", "tools")

  lazy val resolverSettings = Seq(
    resolvers ++= Seq(PlatformReleases, PlatformTools))

  private val defaultCompilationFlags =
    Seq("-deprecation", "-encoding", "UTF-8", "-unchecked")
  private val twoLastScalaVersions = Seq("2.10.6", "2.11.8")
  lazy val compilationSettings = Seq(
    scalacOptions in Compile ++= defaultCompilationFlags,
    crossScalaVersions in Compile := twoLastScalaVersions
  )

  lazy val bintraySettings = Seq(
    bintrayOrganization := Some("scalaplatform"),
    bintrayVcsUrl := platformModuleRepository.value,
    publishTo := (publishTo in bintray).value,
    // Necessary for synchronization with Maven Central
    publishMavenStyle := true,
    bintrayReleaseOnPublish in ThisBuild := false
  )

  /** Define custom release steps and add them to the default pipeline. */
  object PlatformSbtRelease {
    lazy val pluginReleaseSettings = Seq(
      releasePublishArtifactsAction := PgpKeys.publishSigned.value,
      releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        publishArtifacts,
        // TODO(jvican): Add task that checks correct Maven POM
        // releaseStepTask(bintrayEnsurePOM),
        releaseStepTask(bintrayEnsureLicenses),
        releaseStepTask(bintrayRelease),
        setNextVersion,
        commitNextVersion,
        pushChanges
      )
    )
  }

}
