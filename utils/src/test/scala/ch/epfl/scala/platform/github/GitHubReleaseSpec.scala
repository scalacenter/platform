package ch.epfl.scala.platform.github

import ch.epfl.scala.platform.Feedback
import coursier.core.Version
import org.junit.Test
import org.scalatest.junit.JUnitSuite
import GitHubReleaser._

class GitHubReleaseSpec extends JUnitSuite {
  /* Succeeds always because draft is set to true while testing. */
  @Test def createReleaseInGithub(): Unit = {
    val testEnvName = "GITHUB_PLATFORM_TEST_TOKEN"
    val authToken = sys.env.get(testEnvName)
    assert(authToken.isDefined,
           Feedback.undefinedEnvironmentVariable(testEnvName))
    val api = GitHubEndpoint("scalaplatform", "dummy", authToken.get)
    val platformBranch = "master"
    val body = "Look ma', I'm releasing an artifact to GitHub!"
    val release = GitHubRelease(Version("0.1.1.1"), body, platformBranch)
    api.pushRelease(release)
  }

  @Test def matchGitHubUrl(): Unit = {
    "https://github.com/jvican/stoml" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
    "http://github.com/jvican/stoml" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
    "https://www.github.com/jvican/stoml" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
    "http://www.github.com/jvican/stoml" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
    "https://github.com/jvican/stoml/" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
    "http://github.com/jvican/stoml/" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
    "https://www.github.com/jvican/stoml/" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
    "http://www.github.com/jvican/stoml/" match {
      case GitHubUrl(org, name) =>
        assert(org == "jvican")
        assert(name == "stoml")
    }
  }
}
