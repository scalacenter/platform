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
  def getEnvVariable(key: String): Option[String] = sys.env.get(key)
  def toBoolean(presumedBoolean: String) = presumedBoolean.toBoolean
  def toInt(presumedInt: String) = presumedInt.toInt

  // Drone-defined environment variables
  val insideCi = settingKey[Boolean]("Checks if CI is executing the build.")
  insideCi := getEnvVariable("CI").exists(toBoolean)
  val ciName = settingKey[Option[String]]("Get the name of the CI server.")
  ciName := getEnvVariable("CI_NAME")
  val ciRepo = settingKey[Option[String]]("Get the repository run by the CI.")
  ciRepo := getEnvVariable("CI_REPO")
  val ciBranch = settingKey[Option[String]]("Get the current git branch.")
  ciBranch := getEnvVariable("CI_BRANCH")
  val ciCommit = settingKey[Option[String]]("Get the current git commit.")
  ciCommit := getEnvVariable("CI_COMMIT")
  val ciBuildDir = settingKey[Option[String]]("Get the CI build directory.")
  ciBuildDir := getEnvVariable("CI_BUILD_DIR")
  val ciBuildUrl = settingKey[Option[String]]("Get the CI build URL.")
  ciBuildUrl := getEnvVariable("CI_BUILD_URL")
  val ciBuildNumber = settingKey[Option[Int]]("Get the CI build number.")
  ciBuildNumber := getEnvVariable("CI_BUILD_NUMBER").map(toInt)
  val ciPullRequest = settingKey[Option[String]]("Get the pull request id.")
  ciPullRequest := getEnvVariable("CI_PULL_REQUEST")
  val ciJobNumber = settingKey[Option[Int]]("Get the CI job number.")
  ciJobNumber := getEnvVariable("CI_JOB_NUMBER").map(toInt)
  val ciTag = settingKey[Option[String]]("Get the git tag.")
  ciTag := getEnvVariable("CI_TAG")

  // Custom environment variables
  val sonatypeUsername = settingKey[Option[String]]("Get sonatype username.")
  sonatypeUsername := getEnvVariable("SONATYPE_USERNAME")
  val sonatypePassword = settingKey[Option[String]]("Get sonatype password.")
  sonatypePassword := getEnvVariable("SONATYPE_PASSWORD")
  val bintrayUsername = settingKey[Option[String]]("Get bintray username.")
  bintrayUsername := getEnvVariable("BINTRAY_USERNAME")
  val bintrayPassword = settingKey[Option[String]]("Get bintray password.")
  bintrayPassword := getEnvVariable("BINTRAY_PASSWORD")
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
    bintrayVcsUrl := {
      val currentDir = baseDirectory.value
      val getRemoteOrigin = Seq("git", "config", "--get", "remote.origin.url")
      scala.util.Try(Process(getRemoteOrigin, currentDir).!!).toOption
    },
    publishTo := (publishTo in bintray).value,
    // Necessary for synchronization with Maven Central
    publishMavenStyle := true,
    bintrayReleaseOnPublish in ThisBuild := false,
    releaseCrossBuild := true
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
