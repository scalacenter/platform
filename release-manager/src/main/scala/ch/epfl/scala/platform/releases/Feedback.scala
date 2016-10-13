package ch.epfl.scala.platform.releases

object Feedback {
  val UnexpectedFileArgument = "The argument is not an existing file path."
  val InvalidFileContent = "The content of the file is not valid TOML."
  val FailedTargetDirCreation = "Target temp dir could not be created."
  val FailedRepoCreation = "Repository does not exist or could not be cloned."
  val InvalidBranchCheckout = "Checkout to the branch failed."
  val UnexpectedSbtReleaseError = "Execution of the sbt release failed."
}
