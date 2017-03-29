package ch.epfl.scala.platform

import ch.epfl.scala.platform.github.{GitHubRelease, GitHubReleaser}
import ch.epfl.scala.platform.search.{ModuleSearch, ScalaModule}
import ch.epfl.scala.platform.github.GitHubReleaser.GitHubEndpoint

import coursier.core.Version
import coursier.core.Version.Literal
import sbt._
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.Git
import sbtrelease.Version.Bump

import scala.util.control.Exception.catching
import scala.util.Try

object PlatformPlugin extends sbt.AutoPlugin {

  object autoImport extends PlatformSettings

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins =
    bintray.BintrayPlugin &&
      sbtrelease.ReleasePlugin &&
      com.typesafe.sbt.SbtPgp &&
      com.typesafe.tools.mima.plugin.MimaPlugin &&
      coursier.CoursierPlugin &&
      me.vican.jorge.drone.DronePlugin

  override def projectSettings: Seq[Setting[_]] = PlatformKeys.settings
}

trait PlatformSettings {
  // FORMAT: OFF
  val PlatformReleasesRepo = "releases"
  val PlatformNightliesRepo = "nightlies"

  import me.vican.jorge.drone.DronePlugin.autoImport.CIEnvironment
  val platformInsideCi = settingKey[Boolean]("Checks if CI is executing the build.")
  val platformCiEnvironment = settingKey[Option[CIEnvironment]]("Get the Drone environment.")
  val platformModuleName = settingKey[String]("Name for the Platform module in Bintray.")
  val platformModuleTags = settingKey[Seq[String]]("Tags for the platform module package in Bintray.")
  val platformTargetBranch = settingKey[String]("Branch used for the platform release.")
  val platformScalaModule = settingKey[ScalaModule]("Create the ScalaModule from the basic assert info.")
  val platformSbtDefinedVersion = settingKey[Version]("Get the sbt-defined version of the current module.")
  val platformGitHubToken = settingKey[String]("Token to publish releases to GitHub.")
  val platformReleaseNotesDir = settingKey[File]("Directory with the markdown release notes.")
  val platformGitHubRepo = settingKey[Option[(String, String)]]("Get GitHub organization and repository from .git folder.")
  val platformSignArtifact = settingKey[Boolean]("Enable to sign artifacts with the platform pgp key.")
  val platformPgpRings = settingKey[Option[(File, File)]]("Files that store the pgp public and secret ring respectively.")
  val platformDefaultPublicRingName = settingKey[String]("Default file name for fetching the public gpg keys.")
  val platformDefaultPrivateRingName = settingKey[String]("Default file name for fetching the private gpg keys.")

  val platformLogger = taskKey[Logger]("Return the sbt logger.")
  val platformValidatePomData = taskKey[Unit]("Ensure that all the data is available before generating a POM file.")
  val platformCurrentVersion = taskKey[Version]("Get the current version to be released.")
  val platformPreviousArtifacts = taskKey[Set[ModuleID]]("Get `mimaPreviousArtifacts` or fetch latest artifact to run MiMa.")
  val platformLatestPublishedModule = taskKey[Option[ModuleID]]("Fetch latest published stable module.")
  val platformLatestPublishedVersion = taskKey[Option[Version]]("Fetch latest published stable version.")
  val platformRunMiMa = taskKey[Unit]("Run MiMa and report results based on current version.")
  val platformGetReleaseNotes = taskKey[String]("Get the correct release notes for a release.")
  val platformReleaseToGitHub = taskKey[Unit]("Create a release in GitHub.")
  val platformActiveReleaseProcess = taskKey[Option[Seq[ReleaseStep]]]("The active release process if `releaseNightly` or `releaseStable` has been executed.")
  val platformNightlyReleaseProcess = taskKey[Seq[ReleaseStep]]("Define the nightly release process for a Platform module.")
  val platformStableReleaseProcess = taskKey[Seq[ReleaseStep]]("Define the nightly release process for a Platform module.")
  val platformOnMergeReleaseProcess = taskKey[Seq[ReleaseStep]]("Define the nightly release process for a Platform module.")
  val platformReleaseNightly = taskKey[Unit]("Run the nightly release process for a Platform module.")
  val platformReleaseStable = taskKey[Unit]("Run the nightly release process for a Platform module.")
  val platformReleaseOnMerge = taskKey[Unit]("Run the nightly release process for a Platform module.")
  val platformBeforePublishHook = taskKey[Unit]("A hook to customize all the release processes before publishing to Bintray.")
  val platformAfterPublishHook = taskKey[Unit]("A hook to customize all the release processes after publishing to Bintray.")
  // FORMAT: ON
}

object PlatformKeys {

  import PlatformPlugin.autoImport._

  def settings: Seq[Setting[_]] =
    resolverSettings ++ compilationSettings ++ publishSettings ++ platformSettings

  import sbt._, Keys._
  import sbtrelease.ReleasePlugin.autoImport._
  import bintray.BintrayPlugin.autoImport._
  import com.typesafe.tools.mima.plugin.MimaPlugin.autoImport._
  import me.vican.jorge.drone.DronePlugin.autoImport._

  private val PlatformReleases =
    Resolver.bintrayRepo("scalaplatform", PlatformReleasesRepo)
  private val PlatformTools =
    Resolver.bintrayRepo("scalaplatform", "tools")

  lazy val resolverSettings = Seq(
    resolvers ++= Seq(PlatformReleases, PlatformTools))

  private val defaultCompilationFlags =
    Seq("-deprecation", "-encoding", "UTF-8", "-unchecked")
  private val twoLastScalaVersions = Seq("2.10.6", "2.11.8")
  lazy val compilationSettings: Seq[Setting[_]] = Seq(
    scalacOptions in Compile ++= defaultCompilationFlags,
    crossScalaVersions in Compile := twoLastScalaVersions
  )

  lazy val publishSettings: Seq[Setting[_]] = Seq(
    publishTo := (publishTo in bintray).value,
    // Necessary for synchronization with Maven Central
    publishMavenStyle := true,
    publishArtifact in Test := false,
    bintrayReleaseOnPublish in ThisBuild := false,
    bintrayRepository := PlatformReleasesRepo,
    bintrayOrganization := Some("scalaplatform"),
    releaseCrossBuild := true
  ) ++ defaultReleaseSettings

  /** Define custom release steps and add them to the default pipeline. */
  import com.typesafe.sbt.SbtPgp.autoImport._

  lazy val defaultReleaseSettings = Seq(
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    // Empty the default release process to avoid errors
    releaseProcess := Seq.empty[ReleaseStep],
    platformActiveReleaseProcess := None,
    platformNightlyReleaseProcess :=
      PlatformReleaseProcess.Nightly.releaseProcess,
    platformOnMergeReleaseProcess :=
      PlatformReleaseProcess.OnMerge.releaseProcess,
    platformStableReleaseProcess :=
      PlatformReleaseProcess.Stable.releaseProcess
  )

  val emptyModules = Set.empty[ModuleID]
  lazy val platformSettings: Seq[Setting[_]] = Seq(
    platformInsideCi := insideDrone.value,
    platformCiEnvironment := droneEnvironment.value,
    platformLogger := streams.value.log,
    platformModuleName := bintrayPackage.value,
    platformModuleTags := bintrayPackageLabels.value,
    platformTargetBranch := "platform-release",
    platformValidatePomData := {
      if (scmInfo.value.isEmpty)
        throw new NoSuchElementException(Feedback.forceDefinitionOfScmInfo)
      if (licenses.value.isEmpty)
        throw new NoSuchElementException(Feedback.forceValidLicense)
      bintrayEnsureLicenses.value
    },
    platformSbtDefinedVersion := {
      if (version.value.isEmpty)
        sys.error(Feedback.unexpectedEmptyVersion)
      val definedVersion = version.value
      val validatedVersion = for {
        version <- Try(Version(definedVersion)).toOption
        if !version.items.exists(_.isInstanceOf[Literal])
      } yield version
      validatedVersion.getOrElse(
        sys.error(Feedback.invalidVersion(definedVersion)))
    },
    platformCurrentVersion := {
      // Current version is a task whose value changes over time
      platformSbtDefinedVersion.value
    },
    platformScalaModule := {
      val org = organization.value
      val artifact = moduleName.value
      val version = scalaBinaryVersion.value
      ScalaModule(org, artifact, version)
    },
    mimaPreviousArtifacts := {
      val highPriorityArtifacts = mimaPreviousArtifacts.value
      if (highPriorityArtifacts.isEmpty) {
        val targetModule = platformScalaModule.value
        Helper
          .getPublishedArtifacts(targetModule)
          .fold[Set[ModuleID]](
            _ => { println(Feedback.failedConnection); Set.empty },
            identity[Set[ModuleID]]
          )
      } else highPriorityArtifacts
    },
    // TODO: Cache result of this in a setting
    platformLatestPublishedModule := {
      Helper.getPublishedArtifacts(platformScalaModule.value) match {
        case Left(error) => throw error
        case Right(modules) => modules.headOption
      }
    },
    platformLatestPublishedVersion := {
      platformLatestPublishedModule.value
        .map(m => Version(m.revision))
    },
    /* This is a task, not a settings as `mimaPreviousArtifacts.` */
    platformPreviousArtifacts := {
      val previousArtifacts = mimaPreviousArtifacts.value
      // Retry in case sbt boots up without Internet connection
      if (previousArtifacts.nonEmpty) previousArtifacts
      else platformLatestPublishedModule.value.toSet
    },
    releaseVersionBump := Bump.Minor,
    platformRunMiMa := {
      val currentVersion = platformCurrentVersion.value
      val previousVersions = platformPreviousArtifacts.value
        .map(m => Version(m.revision))
      if (previousVersions.isEmpty)
        platformLogger.value.error(Feedback.undefinedPreviousMiMaVersions)
      val canBreakCompat = currentVersion.repr.startsWith("0.")
      val majorCurrentNumber = currentVersion.items.head
      val majorBumps = previousVersions.map(previousVersion =>
        majorCurrentNumber > previousVersion.items.head)
      mimaFailOnProblem := !canBreakCompat && majorBumps.exists(b => b)
      if (canBreakCompat && mimaPreviousArtifacts.value.isEmpty)
        sys.error(Feedback.forceDefinitionOfPreviousArtifacts)
      mimaReportBinaryIssues.value
    },
    platformReleaseNotesDir := baseDirectory.value / "notes",
    platformGetReleaseNotes := {
      val version = platformCurrentVersion.value
      val mdFile = s"$version.md"
      val markdownFile = s"$version.markdown"
      val notes = List(mdFile, markdownFile).foldLeft("") { (acc, curr) =>
        if (acc.nonEmpty) acc
        else {
          val presumedFile = platformReleaseNotesDir.value / curr
          if (!presumedFile.exists) acc
          else IO.read(presumedFile)
        }
      }
      if (notes.isEmpty)
        platformLogger.value.warn(Feedback.emptyReleaseNotes)
      notes
    },
    platformGitHubRepo := {
      releaseVcs.value match {
        case Some(g: Git) =>
          // Fetch git endpoint automatically
          if (g.trackingRemote.isEmpty) sys.error(Feedback.incorrectGitHubRepo)
          val trackingRemote = g.trackingRemote
          val p = g.cmd("config", "remote.%s.url" format trackingRemote)
          val gitResult = p.!!.trim
          gitResult match {
            case GitHubReleaser.SshGitHubUrl(org, repo) => Some(org, repo)
            case GitHubReleaser.HttpsGitHubUrl(org, repo) => Some(org, repo)
            case _ =>
              sys.error(Feedback.incorrectGitHubUrl(trackingRemote, gitResult))
          }
        case Some(vcs) => sys.error("Only git is supported for now.")
        case None => None
      }
    },
    scmInfo := {
      scmInfo.value.orElse {
        platformGitHubRepo.value.map { t =>
          val (org, repo) = t
          val gitHubUrl = GitHubReleaser.generateGitHubUrl(org, repo)
          ScmInfo(url(gitHubUrl), s"scm:git:$gitHubUrl")
        }
      }
    },
    platformGitHubToken := {
      val tokenEnvName = "GITHUB_PLATFORM_TOKEN"
      sys.env.getOrElse(
        tokenEnvName,
        sys.error(Feedback.undefinedEnvironmentVariable(tokenEnvName)))
    },
    platformReleaseToGitHub := {
      def createReleaseInGitHub(org: String, repo: String, token: String) = {
        val endpoint = GitHubEndpoint(org, repo, token)
        val notes = platformGetReleaseNotes.value
        val releaseVersion = platformCurrentVersion.value
        platformLogger.value.info(
          s"Creating a GitHub release for $releaseVersion in $org:$repo.")
        val release = GitHubRelease(releaseVersion, notes)
        endpoint.pushRelease(release)
      }

      val token = platformGitHubToken.value
      val (org, repo) = platformGitHubRepo.value.getOrElse(
        sys.error(Feedback.incorrectGitHubRepo))
      createReleaseInGitHub(org, repo, token)
    },
    platformSignArtifact := true,
    platformDefaultPublicRingName := "platform.pubring.asc",
    platformDefaultPrivateRingName := "platform.secring.asc",
    platformPgpRings := {
      val homeFolder = System.getProperty("user.home")
      if (homeFolder.isEmpty) sys.error(Feedback.expectedCustomRing)
      val gpgFolder = file(s"$homeFolder/.gnupg")
      val publicRing = gpgFolder / platformDefaultPublicRingName.value
      val privateRing = gpgFolder / platformDefaultPrivateRingName.value
      Some(publicRing -> privateRing)
    },
    pgpSigningKey := {
      val PlatformPgpKey = "11BCFDCC60929524"
      if (platformSignArtifact.value) {
        Some(new java.math.BigInteger(PlatformPgpKey, 16).longValue)
      } else None
    },
    pgpPassphrase := {
      if (platformSignArtifact.value)
        sys.env.get("PLATFORM_PGP_PASSPHRASE").map(_.toCharArray)
      else None
    },
    pgpPublicRing := {
      if (platformSignArtifact.value) {
        Helper.getPgpRingFile(platformCiEnvironment.value,
                              platformPgpRings.value.map(_._1),
                              platformDefaultPublicRingName.value)
      } else pgpPublicRing.value
    },
    pgpSecretRing := {
      if (platformSignArtifact.value) {
        Helper.getPgpRingFile(platformCiEnvironment.value,
                              platformPgpRings.value.map(_._2),
                              platformDefaultPrivateRingName.value)
      } else pgpSecretRing.value
    },
    platformBeforePublishHook := {},
    platformAfterPublishHook := {},
    platformReleaseOnMerge :=
      Helper.runCommand(PlatformReleaseProcess.OnMerge.Alias)(state.value),
    platformReleaseStable :=
      Helper.runCommand(PlatformReleaseProcess.Stable.Alias)(state.value),
    platformReleaseNightly :=
      Helper.runCommand(PlatformReleaseProcess.Nightly.Alias)(state.value),
    commands += PlatformReleaseProcess.releaseCommand
  )

  object Helper {
    def getPublishedArtifacts(
        targetModule: ScalaModule): Either[Throwable, Set[ModuleID]] = {
      catching(classOf[java.net.SocketException]).either {
        val response = ModuleSearch.searchLatest(targetModule)
        val moduleResponse = response.map(_.map(rmod =>
          targetModule.orgId %% targetModule.artifactId % rmod.latest_version))
        moduleResponse
          .map(_.map(Set[ModuleID](_)).getOrElse(emptyModules))
          .getOrElse(emptyModules)
      }
    }

    def getPgpRingFile(ciEnvironment: Option[CIEnvironment],
                       customRing: Option[File],
                       defaultRingFileName: String): File = {
      ciEnvironment
        .map(_.rootDir / ".gnupg" / defaultRingFileName)
        .orElse(customRing)
        .getOrElse(sys.error(Feedback.expectedCustomRing))
    }

    def runCommand(command: String): State => State = { st: State =>
      import sbt.complete.Parser
      @annotation.tailrec
      def runCommand0(command: String, state: State): State = {
        val nextState = Parser.parse(command, state.combinedParser) match {
          case Right(cmd) => cmd()
          case Left(msg) =>
            throw sys.error(s"Invalid programmatic input:\n$msg")
        }
        nextState.remainingCommands.toList match {
          case Nil => nextState
          case head :: tail =>
            runCommand0(head, nextState.copy(remainingCommands = tail))
        }
      }
      runCommand0(command, st.copy(remainingCommands = Nil))
        .copy(remainingCommands = st.remainingCommands)
    }
  }
}
