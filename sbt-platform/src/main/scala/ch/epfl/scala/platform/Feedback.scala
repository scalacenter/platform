package ch.epfl.scala.platform

object Feedback {
  val PlatformRepo = "https://github.com/scalacenter/platform-staging."
  val VersioningTip =
    "Make sure your versioning scheme follows semantic versioning."
  val BugReport =
    s"Please, report this bug at $PlatformRepo."

  def skipMiMa(name: String, version: String): String =
    s"Skip MiMa checks because module $name $version starts with '0' and can break bincompat."

  def versionIsAlreadyPublished(version: String) =
    s"The version $version is already published. Try another one."

  def undefinedEnvironmentVariable(name: String) =
    s"Undefined environment variable $name."

  def invalidVersion(version: String) =
    s"Version $version is incorrect. $VersioningTip."

  def unexpectedVersionInteraction(version: String) =
    s"Version $version is incorrect for either Coursier or sbtrelease. $BugReport."

  def invalidOrNonExistingVersions(version: String, next: String) = {
    if (version.isEmpty && next.isEmpty)
      s"Version $version and $next are undefined. $VersioningTip."
    else s"Version $version and $next are invalid. $VersioningTip."
  }

  def onlyCiCommand(command: String) =
    s"The command $command can only be invoked in the CI."

  def incorrectGitHubUrl(remote: String, res: String) =
    s"Your Git remote branch $remote is incorrect: $res."

  val missingReleaseProcess =
    s"Expected the release process as command argument. $BugReport."

  def unexpectedReleaseProcess(process: String) =
    s"The release process $process does not exist."
  val undefinedVersion =
    "Undefined version before releasing. Did you forget to define it?"

  val failedConnection =
    "Previous artifacts could not be fetched from Bintray. It seems there is no connection to Internet."

  val validVersionNotFound =
    "The provided version has not been validated. Run `validateAndSetVersion` before."

  val unexpectedEmptyVersion =
    "The sbt-defined version is empty. Set a well-formatted version in `version.sbt`."

  val forceDefinitionOfScmInfo =
    "Set the setting `scmInfo` manually for the POM file generation."

  val forceValidLicense =
    "Maven Central requires your POM files to define a valid license."

  val forceDefinitionOfPreviousArtifacts =
    "Unexpected empty `mimaPreviousArtifacts`. Set it to perform MiMa checks"

  val incorrectGitHubRepo =
    "Your git repo does not have a remote branch, set it before continuing."

  val expectedScmInfo =
    "Set `scmInfo` before proceeding, it's required for a correct release process."

  val noHomeForRings =
    "Undefined $HOME disallows the default pgp ring folder. Set it or redefine `platformPgpRings`."

  val expectedCustomRing =
    "Default rings were not found. Define `platformPgpRings` to point to existing pgp rings."

  val undefinedPreviousMiMaVersions =
    "MiMa could not find previous versions. MiMa will not be executed."

  val undefinedCommitHash =
    "The commit hash from the drone environment does not exist or is empty."

  val skipRelease = "Detected pull request. Skipping release."
  val emptyReleaseNotes = "Release notes are empty."
  val parsingError = "JSON response could not be parsed."
  val expectedGitTag = "Expected git tag was not found."
  val invalidGitTag = "Provided git tag is not a version."
}
