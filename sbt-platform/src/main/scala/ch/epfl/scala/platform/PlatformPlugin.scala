package ch.epfl.scala.platform

import sbt.{AutoPlugin, Def, PluginTrigger, Plugins, Keys, Compile, Test, ThisBuild, Project}
import java.io.File

object PlatformPlugin extends AutoPlugin {
  val autoImport = AutoImportedKeys
  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins =
    bintray.BintrayPlugin &&
      com.typesafe.sbt.SbtPgp &&
      com.typesafe.tools.mima.plugin.MimaPlugin &&
      ohnosequences.sbt.SbtGithubReleasePlugin &&
      com.thoughtworks.sbtApiMappings.ApiMappings &&
      ch.epfl.scala.sbt.release.ReleaseEarlyPlugin

  override def globalSettings: Seq[Def.Setting[_]] = PlatformPluginImplementation.globalSettings
  override def buildSettings: Seq[Def.Setting[_]] = PlatformPluginImplementation.buildSettings
  override def projectSettings: Seq[Def.Setting[_]] = PlatformPluginImplementation.projectSettings
}

object AutoImportedKeys extends PlatformKeys.PlatformSettings with PlatformKeys.PlatformTasks {
  val noPublishSettings: Seq[Def.Setting[_]] = List(
    Keys.publish := {},
    Keys.publishLocal := {},
    Keys.publishArtifact in Compile := false,
    Keys.publishArtifact in Test := false,
    Keys.publishArtifact := false,
    Keys.skip in Keys.publish := true,
  )

  // This will be updated in future versions of the plugin
  object ScalaVersions {
    val latest210: String = "2.10.6"
    val latest211: String = "2.11.11"
    val latest212: String = "2.12.3"
  }

  def inCompileAndTest(ss: Def.Setting[_]*): Seq[Def.Setting[_]] =
    List(Compile, Test).flatMap(sbt.inConfig(_)(ss))

  // We can add more stuff here in the future, I'm sure it'll be handy
  def makeRoot(project: sbt.Project): sbt.Project =
    project.settings(noPublishSettings)
}

object PlatformKeys {
  import sbt.{settingKey, taskKey}

  trait PlatformSettings {
    val platformRootDir = settingKey[Option[File]]("Tells where the root directory is located.")
    val platformInsideCi = settingKey[Boolean]("Checks if CI is executing the build.")
    val platformGitHubToken = settingKey[String]("Token to publish releases to GitHub.")
    val platformDefaultPublicRingName = settingKey[String]("Default file name of pgp public key.")
    val platformDefaultPrivateRingName = settingKey[String]("Default file name of pgp private key.")
  }

  trait PlatformTasks {}
}

object PlatformPluginImplementation {
  import sbt.{Task, file, fileToRichFile, url, IO, Global}
  import ch.epfl.scala.platform.{AutoImportedKeys => ThisPluginKeys}
  import sbtdynver.DynVerPlugin.{autoImport => DynVerKeys}
  import com.typesafe.tools.mima.plugin.MimaPlugin.{autoImport => MimaKeys}
  import ch.epfl.scala.sbt.release.{AutoImported => ReleaseEarlyKeys}
  import ohnosequences.sbt.SbtGithubReleasePlugin.{autoImport => GithubKeys}
  import com.typesafe.sbt.pgp.PgpKeys

  private final val PlatformReleasesRepo = "releases"
  private final val PlatformNightliesRepo = "nightlies"
  private final val twoLastScalaVersions = List("2.12.3", "2.11.11")

  private val publishArtifactSettings = AutoImportedKeys.inCompileAndTest(
    Keys.publishArtifact in (Compile, Keys.packageDoc) :=
      Defaults.publishDocAndSourceArtifact.value,
    Keys.publishArtifact in (Compile, Keys.packageSrc) :=
      Defaults.publishDocAndSourceArtifact.value
  )

  val projectSettings: Seq[Def.Setting[_]] = List(
    Keys.publishArtifact in Test := false,
    Keys.publishMavenStyle := true,
    Keys.autoAPIMappings := true,
    MimaKeys.mimaReportBinaryIssues := Defaults.mimaReportBinaryIssues.value,
    GithubKeys.githubRelease := Defaults.githubRelease.value,
  ) ++ publishArtifactSettings

  val buildSettings: Seq[Def.Setting[_]] = List(
    Keys.organizationName := "The Scala Platform",
    Keys.organization := "org.scala-lang.platform",
    Keys.organizationHomepage := Some(url("https://platform.scala-lang.org")),
    // Following the SPP process, projects should cross-compile to the last two versions
    Keys.crossScalaVersions := twoLastScalaVersions,
    // Can be removed from here when https://github.com/sbt/sbt-pgp/issues/111 is fixed
    PgpKeys.pgpSigningKey := Defaults.pgpSigningKey.value,
    PgpKeys.pgpPublicRing := Defaults.pgpPublicRing.value,
    PgpKeys.pgpSecretRing := Defaults.pgpSecretRing.value,
  )

  val globalSettings: Seq[Def.Setting[_]] = List(
    ThisPluginKeys.platformRootDir := Some(file("/drone")),
    ThisPluginKeys.platformInsideCi := sys.env.get("CI").nonEmpty,
    ThisPluginKeys.platformGitHubToken := Defaults.platformGitHubToken.value,
    ThisPluginKeys.platformDefaultPublicRingName := Defaults.platformDefaultPublicRingName.value,
    ThisPluginKeys.platformDefaultPrivateRingName := Defaults.platformDefaultPrivateRingName.value,
    PgpKeys.pgpSigningKey := Defaults.pgpSigningKey.value,
    PgpKeys.pgpPublicRing := Defaults.pgpPublicRing.value,
    PgpKeys.pgpSecretRing := Defaults.pgpSecretRing.value,
    ReleaseEarlyKeys.releaseEarlyWith := ReleaseEarlyKeys.SonatypePublisher,
    Keys.onLoadMessage := Defaults.intro,
    // Must be added to sbt-release-early instead of sbt-platform
    Keys.homepage := Keys.scmInfo.value.map(_.browseUrl),
  )

  object Defaults {
    import org.kohsuke.github.GHRelease

    val mimaReportBinaryIssues: Def.Initialize[Task[Unit]] = Def.taskDyn {
      val name = Keys.name.value
      val version = Keys.version.value
      val logger = Keys.streams.value.log
      if (version.startsWith("0.")) Def.task(logger.warn(Feedback.skipMiMa(name, version)))
      else MimaKeys.mimaReportBinaryIssues
    }

    val githubRelease: Def.Initialize[Task[GHRelease]] = Def.taskDyn {
      // If empty string, this should be the fallback, but force it just in case sth changes.
      val version = Keys.version.value
      val githubTask = GithubKeys.githubRelease.toTask(version)
      githubTask.triggeredBy(ReleaseEarlyKeys.releaseEarly)
    }

    val platformDefaultPublicRingName: Def.Initialize[String] = Def.setting("pubring.asc")
    val platformDefaultPrivateRingName: Def.Initialize[String] = Def.setting("secring.asc")

    final val GithubPlatformTokenKey = "GITHUB_PLATFORM_TOKEN"
    private val missingToken = Feedback.undefinedEnvironmentVariable(GithubPlatformTokenKey)
    private val configFile = file(System.getProperty("user.home")) / ".github"
    val platformGitHubToken: Def.Initialize[String] = Def.setting {
      val token = sys.env.getOrElse(GithubPlatformTokenKey, sys.error(missingToken))
      IO.write(configFile, s"oauth = $token")
      token
    }

    private final val PlatformPgpKey = "11BCFDCC60929524"
    val pgpSigningKey: Def.Initialize[Option[Long]] = Def.setting {
      if (!ReleaseEarlyKeys.releaseEarlyNoGpg.value) {
        Some(new java.math.BigInteger(PlatformPgpKey, 16).longValue)
      } else None
    }

    def getPgpRingFile(defaultRingFileName: String,
                       defaultRing: File): Def.Initialize[File] = Def.setting {
      val rootDir = ThisPluginKeys.platformRootDir.value
      ThisPluginKeys.platformRootDir.value
        .map(_ / ".gnupg" / defaultRingFileName)
        .filter(_.exists())
        .getOrElse(defaultRing)
    }

    val pgpPublicRing: Def.Initialize[File] = Def.settingDyn {
      val defaultRing = PgpKeys.pgpPublicRing.value
      if (!ReleaseEarlyKeys.releaseEarlyNoGpg.value) {
        getPgpRingFile(ThisPluginKeys.platformDefaultPublicRingName.value, defaultRing)
      } else Def.setting(defaultRing)
    }

    val pgpSecretRing: Def.Initialize[File] = Def.settingDyn {
      val defaultRing = PgpKeys.pgpSecretRing.value
      if (!ReleaseEarlyKeys.releaseEarlyNoGpg.value) {
        getPgpRingFile(ThisPluginKeys.platformDefaultPrivateRingName.value, defaultRing)
      } else Def.setting(defaultRing)
    }

    /**
      * This setting figures out whether the version is a snapshot or not and configures
      * the source and doc artifacts that are published by the build.
      *
      * Snapshot is a term with no clear definition. In this code, a snapshot is a revision
      * that has either build or time metadata in its representation. In those cases, the
      * build will not publish doc and source artifacts by any of the publishing actions.
      */
    val publishDocAndSourceArtifact: Def.Initialize[Boolean] = Def.setting {
      import sbtdynver.GitDescribeOutput
      def isDynVerSnapshot(gitInfo: Option[GitDescribeOutput], defaultValue: Boolean): Boolean = {
        val isStable = gitInfo.map { info =>
          info.ref.value.startsWith("v") &&
          (info.commitSuffix.distance <= 0 || info.commitSuffix.sha.isEmpty)
        }
        val isNewSnapshot =
          isStable.map(stable => !stable || defaultValue)
        // Return previous snapshot definition in case users has overridden version
        isNewSnapshot.getOrElse(defaultValue)
      }

      // We publish doc and source artifacts if the version is not a snapshot
      !isDynVerSnapshot(DynVerKeys.dynverGitDescribeOutput.value, Keys.isSnapshot.value)
    }

    // For some weird reason, in 2.12 we need to escape '\'
    val intro: String =
      s"""
        |    _____            __         ____  __      __  ____
        |   / ___/_________ _/ /___ _   / __ \\/ /___ _/ /_/ __/___  _________ ___
        |   \\__ \\/ ___/ __ `/ / __ `/  / /_/ / / __ `/ __/ /_/ __ \\/ ___/ __ `__ \\
        |  ___/ / /__/ /_/ / / /_/ /  / ____/ / /_/ / /_/ __/ /_/ / /  / / / / / /
        | /____/\\___/\\__,_/_/\\__,_/  /_/   /_/\\__,_/\\__/_/  \\____/_/  /_/ /_/ /_/
        |
        | *************************************************************************
        | ** Welcome! Read the CONTRIBUTING guide to learn how to use the build. **
        | *************************************************************************
      """.stripMargin
  }
}
