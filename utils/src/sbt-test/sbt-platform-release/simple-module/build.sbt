scalaVersion in Global := "2.11.8"

lazy val checkVariablesContent = taskKey[Unit]("Check the content of variables")
checkVariablesContent := {
  if (insideCi.value) {
    // Check the CI environment
    println(ciName.value)
    assert(ciName.value != None)
    assert(ciName.value.get != "")
    println(ciRepo.value)
    assert(ciRepo.value != None)
    assert(ciRepo.value.get != "")
    println(ciBranch.value)
    assert(ciBranch.value != None)
    assert(ciBranch.value.get != "")
    println(ciCommit.value)
    assert(ciCommit.value != None)
    assert(ciCommit.value.get != "")
    println(ciBuildDir.value)
    assert(ciBuildDir.value != None)
    assert(ciBuildDir.value.get != "")
    println(ciBuildUrl.value)
    assert(ciBuildUrl.value != None)
    assert(ciBuildUrl.value.get != "")
    println(ciBuildNumber.value)
    assert(ciBuildNumber.value != None)
    assert(ciBuildNumber.value.get >= 0)
    println(ciPullRequest.value)
    assert(ciPullRequest.value != None)
    assert(ciPullRequest.value.get != "")
    println(ciJobNumber.value)
    assert(ciJobNumber.value != None)
    assert(ciJobNumber.value.get >= 0)
    println(ciTag.value)
    assert(ciTag.value != None)
    assert(ciTag.value.get != "")
  }
}