package ch.epfl.scala.platform

object Feedback {
  val VersioningTip = "Make sure your versioning scheme follows semantic versioning."

  def versionIsAlreadyPublished(version: String) =
    s"The version $version is already published. Try another one."

  def undefinedEnvironmentVariable(name: String) =
    s"Undefined environment variable $name."

  def malformattedVersion(version: String) = s"Version $version is incorrect. $VersioningTip."

  def invalidOrNonExistingVersions(version: String, next: String) =
    s"Version $version and $next are invalid or have not been defined. $VersioningTip."

  def incorrectGitHubUrl(remote: String, res: String) =
    s"Your Git remote branch $remote is incorrect: $res."

  val unexpectedEmptyVersion =
    "The sbt-defined version is empty. Set a well-formatted version in `version.sbt`."
  val forceDefinitionOfScmInfo =
    "Set the setting `scmInfo` manually for the POM file generation."
  val forceValidLicense =
    "Maven Central requires your POM files to define a valid license."
  val forceDefinitionOfPreviousArtifacts =
    "Unexpected empty `mimaPreviousArtifacts`. Set it to perform MiMa checks"
  val undefinedVersion =
    "No versions are set! Did you pass a release version to `releaseNightly` or set the sbt version setting?"
  val incorrectGitHubRepo =
    "Your git repo does not have a remote branch, set it before continuing."
  val expectedScmInfo =
    "Set `scmInfo` before proceeding, it's required for a correct release process."
  val parsingError = "JSON response could not be parsed."
}
