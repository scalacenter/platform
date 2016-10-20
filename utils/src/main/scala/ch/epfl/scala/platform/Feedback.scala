package ch.epfl.scala.platform

object Feedback {
  val forceDefinitionOfScmInfo =
    "Set the setting `scmInfo` manually for the POM file generation."
  val forceValidLicense =
    "Maven Central requires your POM files to define a valid license."
  val forceDefinitionOfPreviousArtifacts =
    "Unexpected empty `mimaPreviousArtifacts`. Set it to perform MiMa checks"
}
