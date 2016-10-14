package ch.epfl.scala.platform.releases

import stoml.Toml

object Feedback {
  val UnexpectedFileArgument = "The argument is not an existing file path."
  val InvalidFileContent = "The content of the file is not valid TOML."
  val FailedTargetDirCreation = "Target temp dir could not be created."
  val FailedRepoCreation = "Repository does not exist or could not be cloned."
  val InvalidBranchCheckout = "Checkout to the branch failed."
  val UnexpectedSbtReleaseError = "Execution of the sbt release failed."

  def missingModuleAttribute(attribute: String): String =
    s"A module requires an attribute $attribute."
  def unexpectedAttribute(attribute: String,
                          tpe: Toml.Elem,
                          expected: String) =
    s"Unexpected $attribute of type $tpe, expected $expected."
}
