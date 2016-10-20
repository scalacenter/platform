scalaVersion in Global := "2.11.8"

lazy val checkVariablesContent = taskKey[Unit]("Check the content of variables")
checkVariablesContent := {
  if (insideCi.value) {
    // Check the CI environment
    assert(ciName.value != None)
    assert(ciName.value.get != "")
    assert(ciRepo.value != None)
    assert(ciRepo.value.get != "")
    assert(ciBranch.value != None)
    assert(ciBranch.value.get != "")
    assert(ciCommit.value != None)
    assert(ciCommit.value.get != "")
    assert(ciBuildDir.value != None)
    assert(ciBuildDir.value.get != "")
    assert(ciBuildUrl.value != None)
    assert(ciBuildUrl.value.get != "")
    assert(ciBuildNumber.value != None)
    assert(ciBuildNumber.value.get >= 0)
    assert(ciJobNumber.value != None)
    assert(ciJobNumber.value.get >= 0)
    // CI_PULL_REQUEST and CI_TAG may not be defined
  }
}