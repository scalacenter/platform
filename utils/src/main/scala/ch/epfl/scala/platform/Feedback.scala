package ch.epfl.scala.platform

object Feedback {
  val forceDefinitionOfScmInfo =
    "Set the setting `scmInfo` manually for the POM file generation."
  val forceValidLicense =
    "Maven Central requires your POM files to define a valid license."
  val forceDefinitionOfPreviousArtifacts =
    "Unexpected empty `mimaPreviousArtifacts`. Set it to perform MiMa checks"
  val undefinedVersion =
    "No versions are set! Was this release part executed before inquireVersions?"
  def undefinedEnvironmentVariable(name: String) =
    s"Your environment does not define $name. Set it before proceeding!"
  val incorrectGitHubUrl =
    "The value of the setting `scmInfo` is not a GitHub url."
  val expectedScmInfo =
    "Set `scmInfo` before proceeding, it's required for a correct release process."
}
