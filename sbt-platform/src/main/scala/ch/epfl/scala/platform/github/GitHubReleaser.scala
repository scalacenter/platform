package ch.epfl.scala.platform.github

import gigahorse._
import ch.epfl.scala.platform.logger
import coursier.core.Version
import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._

object GitHubReleaser extends GitHubDataTypes with GitHubResources {
  type CirceResult[T] = cats.data.Xor[io.circe.Error, T]
  val HttpsGitHubUrl =
    """https?://(?:www\.)?github\.com/([a-zA-Z0-9]+)/([a-zA-Z0-9]+)/?""".r
  val SshGitHubUrl =
    """git@github.com:([a-zA-Z0-9]+)/([a-zA-Z0-9]+)\.git""".r
  def generateGitHubUrl(org: String, name: String) =
    s"https://github.com/$org/$name"

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
                           isDraft: Boolean = false,
                           preRelease: Version => Boolean = defaultPreRelease)

  case class GitHubEndpoint(owner: String, repo: String, authToken: String)
    extends GithubApi {
    val testing = System.getProperty("platform.test") == "true"

    val defaultGithubHeaders = Seq(
      HeaderNames.ACCEPT -> "application/vnd.github.v3+json",
      HeaderNames.USER_AGENT -> s"$repo",
      HeaderNames.AUTHORIZATION -> s"token $authToken"
    ).toMap.mapValues(List(_))

    case class ReleaseRequest(tag_name: String, target_commitish: String,
                              name: String, body: String, draft: Boolean,
                              prerelease: Boolean)

    object ReleaseRequest {
      def apply(release: GitHubRelease): ReleaseRequest = {
        val versionNumber = s"v${release.version.repr}"
        ReleaseRequest(versionNumber, release.branch, versionNumber, release.body, release.isDraft || testing, release.preRelease(release.version))
      }
    }

    private def pushReleaseRequest(release: ReleaseRequest): Request = {
      logger.elem(s"The payload of the release API is:\n$release")
      Gigahorse
        .url(s"$baseUrl/repos/$owner/$repo/releases")
        .addHeaders(defaultGithubHeaders)
        .post(release.asJson.noSpaces)
    }

    private def pushResourceRequest(resource: Resource,
                                    releaseId: Int): Request = {
      logger.elem(s"Pushing a resource to $releaseId in $owner/$repo: $resource.")
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
          http.run(pushReleaseRequest(ReleaseRequest(release)),
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
