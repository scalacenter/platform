// This is obviously a trick so that `platformFetchPreviousArtifacts` succeed
organization := "me.vican.jorge"
name := "stoml"
scalaVersion in Global := "2.11.8"

licenses := Seq("MPL-2.0" -> url("http://opensource.org/licenses/MPL-2.0"))

lazy val checkVariablesContent =
  taskKey[Unit]("Check the content of variables")
checkVariablesContent := {
  if (insideCi.value) {
    assert(ciEnvironment.value != None)
    val ciEnv = ciEnvironment.value.get
    assert(ciEnv.rootDir.exists)
    assert(ciEnv.name != "")
    assert(ciEnv.repo != "")
    assert(ciEnv.branch != "")
    assert(ciEnv.commit != "")
    assert(ciEnv.buildDir != "")
    assert(ciEnv.buildUrl != "")
    assert(ciEnv.buildNumber >= 0)
    assert(ciEnv.jobNumber >= 0)
    // Don't check optional pull request and tag envars
  }
}

lazy val checkPreviousArtifact = taskKey[Unit]("Check mimaPreviousArtifacts is set.")
checkPreviousArtifact := {
  assert(mimaPreviousArtifacts.value.nonEmpty)
}
