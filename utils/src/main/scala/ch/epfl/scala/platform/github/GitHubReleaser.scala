package ch.epfl.scala.platform.github

import gigahorse._
import ch.epfl.scala.platform.logger
import coursier.core.Version

object GitHubReleaser extends GitHubDataTypes with GitHubResources {
  type CirceResult[T] = cats.data.Xor[io.circe.Error, T]

  trait GithubApi {
    val baseUrl = "https://api.github.com"
    val uploadsUrl = "https://uploads.github.com"

    def pushRelease(release: GitHubRelease): CirceResult[ReleaseCreated]
    def pushResourceToRelease(resource: Resource, releaseId: Int): Boolean
  }

  val defaultPreRelease = (v: Version) => false
  case class GitHubRelease(version: Version,
                           body: String,
                           branch: String = "platform-release",
                           preRelease: Version => Boolean = defaultPreRelease)

  case class GitHubEndpoint(owner: String, repo: String, authToken: String)
      extends GithubApi {
    val defaultGithubHeaders = Seq(
      HeaderNames.ACCEPT -> "application/vnd.github.v3+json",
      HeaderNames.USER_AGENT -> s"$repo",
      HeaderNames.AUTHORIZATION -> s"token $authToken"
    ).toMap.mapValues(List(_))

    val testing = System.getProperty("platform.test") == "true"
    private def pushReleaseRequest(release: GitHubRelease): Request = {
      val versionNumber = s"v${release.version.repr}"
      val payload = s"""
        |{
        |  "tag_name": "$versionNumber",
        |  "target_commitish": "${release.branch}",
        |  "name": "$versionNumber",
        |  "body": "${release.body}",
        |  "draft": $testing,
        |  "prerelease": ${release.preRelease(release.version)}
        |}
      """.stripMargin
      logger.elem(s"The payload of the release API is:\n$payload")
      Gigahorse
        .url(s"$baseUrl/repos/$owner/$repo/releases")
        .addHeaders(defaultGithubHeaders)
        .post(payload)
    }

    private def pushResourceRequest(resource: Resource,
                                    releaseId: Int): Request = {
      Gigahorse
        .url(s"$uploadsUrl/repos/$owner/$repo/releases/$releaseId/assets")
        .addHeaders(defaultGithubHeaders)
        .addHeader(HeaderNames.CONTENT_TYPE -> resource.contentType)
        .addQueryString("name" -> resource.name, "label" -> resource.label)
        .post(resource.file)
    }

    import scala.concurrent._
    import scala.concurrent.duration._

    import io.circe._
    import generic.auto._
    import parser._

    private val DefaultTimeout = 60.seconds
    private def awaitResult[T](f: Awaitable[T]) =
      Await.result[T](f, DefaultTimeout)

    /** Submit a release to the Github interface, with artifacts and release notes. */
    def pushRelease(release: GitHubRelease): CirceResult[ReleaseCreated] = {
      Gigahorse.withHttp(Gigahorse.config) { http =>
        awaitResult(
          http.run(pushReleaseRequest(release),
                   Gigahorse.asString andThen decode[ReleaseCreated]))
      }
    }

    /** Add a resource to an already-created GitHub release. */
    def pushResourceToRelease(resource: Resource, releaseId: Int): Boolean = {
      Gigahorse.withHttp(Gigahorse.config) { http =>
        awaitResult(
          http.run(pushResourceRequest(resource, releaseId),
                   r => r.status == Status.CREATED)
        )
      }
    }
  }
}
