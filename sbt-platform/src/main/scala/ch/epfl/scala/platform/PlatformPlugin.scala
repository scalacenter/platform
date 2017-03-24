package ch.epfl.scala.platform

import ch.epfl.scala.platform
import ch.epfl.scala.platform.github.{GitHubRelease, GitHubReleaser}
import ch.epfl.scala.platform.search.{ModuleSearch, ScalaModule}
import ch.epfl.scala.platform.util.Error
import ch.epfl.scala.platform.github.GitHubReleaser.GitHubEndpoint
import cats.data.Xor
import coursier.core.Version
import coursier.core.Version.{Literal, Qualifier}
import org.joda.time.DateTime
import sbt._
import sbt.complete.Parser
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import sbtrelease.{Git, ReleaseStateTransformations}
import sbtrelease.Version.Bump

import scala.util.{Random, Try}

object PlatformPlugin extends sbt.AutoPlugin {

  object autoImport extends PlatformSettings

  override def trigger = allRequirements

  override def requires =
    bintray.BintrayPlugin &&
      sbtrelease.ReleasePlugin &&
      com.typesafe.sbt.SbtPgp &&
      com.typesafe.tools.mima.plugin.MimaPlugin

  override def projectSettings = PlatformKeys.settings
}

trait PlatformSettings {

  case class RepositoryInfo(fullName: String, owner: String, name: String,
                            scm: String, link: String, avatar: String,
                            branch: String, isPrivate: Boolean,
                            isTrusted: Boolean)

  case class AuthorInfo(author: String, email: String, avatar: String)

  case class CommitInfo(sha: String, ref: String, branch: String, link: String,
                        message: String, author: AuthorInfo)

  case class BuildInfo(number: Int, event: String, status: String, link: String,
                       created: String, started: String, finished: String,
                       prevBuildStatus: String, prevBuildNumber: Int,
                       prevCommitSha: String)

  case class CIEnvironment(rootDir: File,
                           arch: String,
                           repo: RepositoryInfo,
                           commit: CommitInfo,
                           build: BuildInfo,
                           remoteUrl: String,
                           pullRequest: Option[Int],
                           tag: Option[String])

  // FORMAT: OFF
  val defaultDroneWorkspace = "/drone"
  val platformInsideCi = settingKey[Boolean]("Checks if CI is executing the build.")
  val platformCiEnvironment = settingKey[Option[CIEnvironment]]("Get the Drone environment.")
  val platformReleaseOnMerge = settingKey[Boolean]("Release on every PR merge.")
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
  val platformNightlyReleaseProcess = taskKey[Seq[ReleaseStep]]("The nightly release process for a Platform module.")
  val platformStableReleaseProcess = taskKey[Seq[ReleaseStep]]("The nightly release process for a Platform module.")
  val platformBeforePublishHook = taskKey[Unit]("A hook to customize all the release processes before publishing to Bintray.")
  val platformAfterPublishHook = taskKey[Unit]("A hook to customize all the release processes after publishing to Bintray.")
  // FORMAT: ON
  //val checkJsonMethod = taskKey[Unit]("asdjkf;lakjdf")
}

object PlatformKeys {

  import PlatformPlugin.autoImport._

  def settings: Seq[Setting[_]] =
    resolverSettings ++ compilationSettings ++ publishSettings ++ platformSettings

  import sbt._, Keys._
  import Helper._
  import sbtrelease.ReleasePlugin.autoImport._
  import bintray.BintrayPlugin.autoImport._
  import com.typesafe.tools.mima.plugin.MimaPlugin.autoImport._

  private val PlatformReleases =
    Resolver.bintrayRepo("scalaplatform", "modules-releases")
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
    bintrayRepository := "modules-releases",
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
    platformStableReleaseProcess :=
      PlatformReleaseProcess.Stable.releaseProcess
  )

  val emptyModules = Set.empty[ModuleID]
  lazy val platformSettings: Seq[Setting[_]] = Seq(
    platformInsideCi := getEnvVariable("DRONE").exists(_.toBoolean),
    platformCiEnvironment := {
      if (!platformInsideCi.value) None
      else {
        val repositoryInfo = for {
          ciRepo <- getEnvVariable("DRONE_REPO")
          ciRepoOwner <- getEnvVariable("DRONE_REPO_OWNER")
          ciRepoName <- getEnvVariable("DRONE_REPO_NAME")
          ciRepoScm <- getEnvVariable("DRONE_REPO_SCM")
          ciRepoLink <- getEnvVariable("DRONE_REPO_LINK")
          ciRepoAvatar <- getEnvVariable("DRONE_REPO_AVATAR")
          ciRepoBranch <- getEnvVariable("DRONE_REPO_BRANCH")
          ciRepoPrivate <- getEnvVariable("DRONE_REPO_PRIVATE").map(_.toBoolean)
          ciRepoTrusted <- getEnvVariable("DRONE_REPO_TRUSTED").map(_.toBoolean)
        } yield RepositoryInfo(ciRepo, ciRepoOwner, ciRepoName, ciRepoScm, ciRepoLink, ciRepoAvatar, ciRepoBranch, ciRepoPrivate, ciRepoTrusted)

        val buildInfo = for {
          ciBuildNumber <- getEnvVariable("DRONE_BUILD_NUMBER").map(_.toInt)
          ciBuildEvent <- getEnvVariable("DRONE_BUILD_EVENT")
          ciBuildStatus <- getEnvVariable("DRONE_BUILD_STATUS")
          ciBuildLink <- getEnvVariable("DRONE_BUILD_LINK")
          ciBuildCreated <- getEnvVariable("DRONE_BUILD_CREATED")
          ciBuildStarted <- getEnvVariable("DRONE_BUILD_STARTED")
          ciBuildFinished <- getEnvVariable("DRONE_BUILD_FINISHED")
          ciPrevBuildStatus <- getEnvVariable("DRONE_PREV_BUILD_STATUS")
          ciPrevBuildNumber <- getEnvVariable("DRONE_PREV_BUILD_NUMBER").map(_.toInt)
          ciPrevCommitSha <- getEnvVariable("DRONE_PREV_COMMIT_SHA")
        } yield BuildInfo(ciBuildNumber, ciBuildEvent, ciBuildStatus, ciBuildLink, ciBuildCreated, ciBuildStarted, ciBuildFinished, ciPrevBuildStatus, ciPrevBuildNumber, ciPrevCommitSha)

        val commitInfo = for {
          ciCommitSha <- getEnvVariable("DRONE_COMMIT_SHA")
          ciCommitRef <- getEnvVariable("DRONE_COMMIT_REF")
          ciCommitBranch <- getEnvVariable("DRONE_COMMIT_BRANCH")
          ciCommitLink <- getEnvVariable("DRONE_COMMIT_LINK")
          ciCommitMessage <- getEnvVariable("DRONE_COMMIT_MESSAGE")
          ciAuthor <- getEnvVariable("DRONE_COMMIT_AUTHOR")
          ciAuthorEmail <- getEnvVariable("DRONE_COMMIT_AUTHOR_EMAIL")
          ciAuthorAvatar <- getEnvVariable("COMMIT_AUTHOR_AVATAR")
        } yield CommitInfo(ciCommitSha, ciCommitRef, ciCommitBranch, ciCommitLink, ciCommitMessage, AuthorInfo(ciAuthor, ciAuthorEmail, ciAuthorAvatar))

        for {
          ciDroneArch <- getEnvVariable("DRONE_ARCH")
          ciRepositoryInfo <- repositoryInfo
          ciCommitInfo <- commitInfo
          ciBuildInfo <- buildInfo
          ciRemoteUrl <- getEnvVariable("DRONE_REMOTE_URL")
        } yield
          CIEnvironment(file(defaultDroneWorkspace),
            ciDroneArch,
            ciRepositoryInfo,
            ciCommitInfo,
            ciBuildInfo,
            ciRemoteUrl,
            getEnvVariable("DRONE_PULL_REQUEST").map(_.toInt),
            getEnvVariable("DRONE_TAG"))
      }
    },
    platformLogger := streams.value.log,
    platformReleaseOnMerge := false, // By default, disabled
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
        /* This is a setting because modifies previousArtifacts, so we protect
         * ourselves from errors if users don't have connection to Internet. */
        val targetModule = platformScalaModule.value
        Helper.getPublishedArtifacts(targetModule)
      } else highPriorityArtifacts
    },
    platformLatestPublishedModule := {
      Helper.getPublishedArtifacts(platformScalaModule.value).headOption
    },
    platformLatestPublishedVersion := {
      platformLatestPublishedModule.value
        .map(m => Version(m.revision))
    },
    /* This is a task, not a settings as `mimaPreviousArtifacts.` */
    platformPreviousArtifacts := {
      val previousArtifacts = mimaPreviousArtifacts.value
      // Retry in case where sbt boots up without Internet connection
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

/*
    checkJsonMethod := {
      println("JSON METHODS")
      println(classOf[org.json4s.native.JsonMethods].getDeclaredMethods.mkString("\n"))
      println(classOf[org.json4s.native.JsonMethods].getProtectionDomain.getCodeSource.getLocation)
      println("JSON AST CLASS")
      println(classOf[org.json4s.JsonAST.JValue].getDeclaredMethods.mkString("\n"))
    },
*/

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
      sys.env.getOrElse(tokenEnvName,
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
    commands += PlatformReleaseProcess.releaseCommand
  ) ++ PlatformReleaseProcess.aliases

  object Helper {

    implicit class XtensionCoursierVersion(v: Version) {
      def toSbtRelease: sbtrelease.Version = {
        val repr = v.repr
        sbtrelease.Version(repr).getOrElse(
          sys.error(Feedback.unexpectedVersionInteraction(repr)))
      }
    }

    implicit class XtensionSbtReleaseVersion(v: sbtrelease.Version) {
      def toCoursier: Version = validateVersion(v.string)
    }

    def getEnvVariable(key: String): Option[String] = sys.env.get(key)

    def getDroneEnvVariableOrDie(key: String) = {
      getEnvVariable(key).getOrElse(
        sys.error(Feedback.undefinedEnvironmentVariable(key)))
    }

    def getDroneEnvVariableOrDie[T](key: String, conversion: String => T): T = {
      getEnvVariable(key)
        .map(conversion)
        .getOrElse(sys.error(Feedback.undefinedEnvironmentVariable(key)))
    }

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

    def getPublishedArtifacts(targetModule: ScalaModule): Set[ModuleID] = {
      val response = ModuleSearch.searchLatest(targetModule)
      val moduleResponse = response.map(_.map(rmod =>
        targetModule.orgId %% targetModule.artifactId % rmod.latest_version))
      moduleResponse
        .map(_.map(Set[ModuleID](_)).getOrElse(emptyModules))
        .getOrElse(emptyModules)
    }

    def getPgpRingFile(ciEnvironment: Option[CIEnvironment],
                       customRing: Option[File],
                       defaultRingFileName: String) = {
      ciEnvironment.map(_.rootDir / ".gnupg" / defaultRingFileName)
        .orElse(customRing).getOrElse(sys.error(Feedback.expectedCustomRing))
    }
  }

  object PlatformReleaseProcess {

    import ReleaseKeys._
    import sbtrelease.Utilities._
    import sbtrelease.ReleaseStateTransformations._

    // Attributes for the custom release command
    val releaseProcessAttr = AttributeKey[String]("releaseProcess")
    val commandLineVersion = AttributeKey[Option[String]]("commandLineVersion")
    val validReleaseVersion = AttributeKey[Version]("validatedReleaseVersions")

    private def generateUbiquituousVersion(version: String, st: State) = {
      val ci = st.extract.get(platformCiEnvironment)
      val unique = ci.map(_.build.number.toString)
        .getOrElse(Random.nextLong.abs.toString)
      s"$version-$unique"
    }

    /** Update the SBT tasks and attribute that holds the current version value. */
    def updateCurrentVersion(definedVersion: Version, st: State): State = {
      val updated = st.extract.append(
        Seq(platformCurrentVersion := definedVersion), st)
      updated.put(validReleaseVersion, definedVersion)
    }

    val decideAndValidateVersion: ReleaseStep = { (st: State) =>
      val logger = st.globalLogging.full
      val userVersion = st.get(commandLineVersion).flatten.map(validateVersion)
      val definedVersion = userVersion.getOrElse(st.extract.get(platformSbtDefinedVersion))
      // TODO(jvican): Make sure minor and major depend on platform version
      val bumpFunction = st.extract.get(releaseVersionBump)
      val nextVersion = bumpFunction.bump.apply(definedVersion.toSbtRelease).toCoursier
      logger.info(s"Current version is $definedVersion.")
      logger.info(s"Next version is set to $nextVersion.")
      updateCurrentVersion(definedVersion, st)
        .put(versions, (definedVersion.repr, nextVersion.repr))
    }

    val checkVersionIsNotPublished: ReleaseStep = { (st: State) =>
      val definedVersion = st
        .get(validReleaseVersion)
        .getOrElse(sys.error(Feedback.undefinedVersion))
      val module = st.extract.get(platformScalaModule)
      // TODO(jvican): Improve error handling here
      ModuleSearch
        .exists(module, definedVersion)
        .flatMap { exists =>
          if (!exists) Xor.right(st)
          else Xor.left(Error(Feedback.versionIsAlreadyPublished(definedVersion.toString)))
        }
        .fold(e => sys.error(e.msg), identity)
    }

    import ReleaseKeys._

    object PlatformParseResult {

      case class ReleaseProcess(value: String) extends ParseResult

    }

    import sbt.complete.DefaultParsers.{Space, token, StringBasic}

    val releaseProcessToken = "release-process"
    val ReleaseProcess: Parser[ParseResult] =
      (Space ~> token("release-process") ~> Space ~> token(
        StringBasic,
        "<nightly | stable>")) map PlatformParseResult.ReleaseProcess

    val releaseParser: Parser[Seq[ParseResult]] = {
      (ReleaseProcess ~ (ReleaseVersion | SkipTests | CrossBuild).*).map {
        args =>
          val (mandatoryArg, optionalArgs) = args
          mandatoryArg +: optionalArgs
      }
    }

    def setAndReturnReleaseParts(releaseProcess: TaskKey[Seq[ReleaseStep]],
                                 st: State) = {
      val extracted = Project.extract(st)
      val (st1, parts) = extracted.runTask(releaseProcess, st)
      // Set the active release process before returning the release parts
      val active = platformActiveReleaseProcess := Some(parts)
      val st2 = extracted.append(active, st1)
      (st2, parts)
    }

    val FailureCommand = "--failure--"
    val releaseCommand: Command =
      Command("releaseModule")(_ => releaseParser) { (st, args) =>
        val logger = st.globalLogging.full
        val extracted = Project.extract(st)
        val crossEnabled = extracted.get(releaseCrossBuild) ||
          args.contains(ParseResult.CrossBuild)
        val selectedReleaseProcess = args.collectFirst {
          case PlatformParseResult.ReleaseProcess(value) => value
        }.getOrElse(Feedback.missingReleaseProcess)

        val startState = st
          .copy(onFailure = Some(FailureCommand))
          .put(releaseProcessAttr, selectedReleaseProcess)
          .put(skipTests, args.contains(ParseResult.SkipTests))
          .put(cross, crossEnabled)
          .put(commandLineVersion, args.collectFirst {
            case ParseResult.ReleaseVersion(value) => value
          })

        val (updatedState, releaseParts) = {
          selectedReleaseProcess.toLowerCase match {
            case "nightly" =>
              logger.info("Nightly release process has been selected.")
              setAndReturnReleaseParts(platformNightlyReleaseProcess, startState)
            case "stable" =>
              logger.info("Stable release process has been selected.")
              setAndReturnReleaseParts(platformStableReleaseProcess, startState)
            case rp => sys.error(Feedback.unexpectedReleaseProcess(rp))
          }
        }

        val initialChecks = releaseParts.map(_.check)
        val process = releaseParts.map { step =>
          if (step.enableCrossBuild && crossEnabled) {
            filterFailure(
              ReleaseStateTransformations.runCrossBuild(step.action)) _
          } else filterFailure(step.action) _
        }

        val removeFailureCommand = { s: State =>
          s.remainingCommands match {
            case FailureCommand :: tail => s.copy(remainingCommands = tail)
            case _ => s
          }
        }
        initialChecks.foreach(_ (updatedState))
        Function.chain(process :+ removeFailureCommand)(updatedState)
      }

    val aliases = {
      // Aliases that use the custom release command
      PlatformReleaseProcess.Nightly.releaseCommand ++
        PlatformReleaseProcess.Stable.releaseCommand
    }

    object Nightly {
      val tagAsNightly: ReleaseStep = { (st0: State) =>
        val (st, logger) = st0.extract.runTask(platformLogger, st0)
        val targetVersion = st
          .get(validReleaseVersion)
          .getOrElse(sys.error(Feedback.validVersionNotFound))
        val now = DateTime.now()
        val month = now.dayOfMonth().get
        val day = now.monthOfYear().get
        val year = now.year().get
        val template = s"${targetVersion.repr}-alpha-$year-$month-$day"
        val nightlyVersion =
          if (!platform.testing) template
          else generateUbiquituousVersion(template, st)
        val generatedVersion = targetVersion.copy(nightlyVersion)
        logger.info(s"Nightly version is set to ${generatedVersion.repr}.")
        val previousVersions =
          st.get(versions).getOrElse(sys.error(Feedback.undefinedVersion))
        updateCurrentVersion(generatedVersion, st)
          .put(versions, (generatedVersion.repr, previousVersions._2))
      }

      val releaseCommand =
        addCommandAlias("releaseNightly",
          "releaseModule release-process nightly")

      val releaseProcess = {
        Seq[ReleaseStep](
          decideAndValidateVersion,
          tagAsNightly,
          checkVersionIsNotPublished,
          setReleaseVersion,
          releaseStepTask(platformValidatePomData),
          checkSnapshotDependencies,
          runTest,
          releaseStepTask(platformRunMiMa),
          releaseStepTask(platformBeforePublishHook),
          publishArtifacts,
          releaseStepTask(platformAfterPublishHook),
          releaseStepTask(bintrayRelease)
        )
      }
    }

    object Stable {
      def cleanUpTag(tag: String): String =
        if (tag.startsWith("v")) tag.replaceFirst("v", "") else tag

      val setVersionFromGitTag: ReleaseStep = { (st: State) =>
        val logger = st.globalLogging.full
        val commandLineDefinedVersion = st.get(commandLineVersion)
        // Command line version always takes precedence
        val specifiedVersion = commandLineDefinedVersion.flatten match {
          case Some(version) if version.nonEmpty => version
          case None =>
            val ciInfo = st.extract.get(platformCiEnvironment)
            ciInfo.map(e => e.tag) match {
              case Some(Some(versionTag)) => versionTag
              case Some(None) => sys.error(Feedback.expectedGitTag)
              case None => sys.error(Feedback.onlyCiCommand("releaseStable"))
            }
        }

        // TODO(jvican): Separate testing from main logic
        val stableVersion = if (platform.testing) {
          generateUbiquituousVersion(cleanUpTag(specifiedVersion), st)
        } else cleanUpTag(specifiedVersion)
        logger.info(s"Version read from the git tag: $stableVersion")
        st.put(commandLineVersion, Some(stableVersion))
      }

      val releaseCommand =
        addCommandAlias("releaseStable",
          "releaseModule release-process stable")

      val releaseProcess = {
        Seq[ReleaseStep](
          setVersionFromGitTag,
          decideAndValidateVersion,
          checkVersionIsNotPublished,
          setReleaseVersion,
          releaseStepTask(platformValidatePomData),
          checkSnapshotDependencies,
          runTest,
          releaseStepTask(platformRunMiMa),
          releaseStepTask(platformBeforePublishHook),
          publishArtifacts,
          releaseStepTask(platformReleaseToGitHub),
          releaseStepTask(platformAfterPublishHook),
          releaseStepTask(bintrayRelease)
        )
      }

    }

  }

}
