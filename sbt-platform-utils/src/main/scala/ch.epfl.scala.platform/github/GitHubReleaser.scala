package ch.epfl.scala.platform.github

import ch.epfl.scala.platform.logger
import com.ning.http.client.Response
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import dispatch._
import dispatch.Defaults._

import scala.util.matching.Regex

object GitHubReleaser extends GitHubDataTypes with GitHubResources {
  type CirceResult[T] = cats.data.Xor[io.circe.Error, T]
  val HttpsGitHubUrl: Regex =
    """https?://(?:www\.)?github\.com/([a-zA-Z0-9-]+)/([a-zA-Z0-9-_.]+)/?""".r
  val SshGitHubUrl: Regex =
    """git@github.com:([a-zA-Z0-9-]+)/([a-zA-Z0-9-_.]+)\.git""".r

  def generateGitHubUrl(org: String, repo: String) =
    s"https://github.com/$org/$repo"

  trait GithubApi {
    val baseUrl = "https://api.github.com"
    val uploadsUrl = "https://uploads.github.com"

    def pushRelease(release: GitHubRelease): CirceResult[ReleaseCreated]

    def pushResourceToRelease(resource: Resource, releaseId: Int): Boolean
  }

  case class GitHubEndpoint(owner: String, repo: String, authToken: String)
      extends GithubApi {

    private def pushReleaseRequest(release: ReleaseRequest): Req = {
      logger.elem(s"The payload of the release API is:\n$release")
      url(s"$baseUrl/repos/$owner/$repo/releases")
        .POST
        .addHeader("Accept", "application/vnd.github.v3+json")
        .addHeader("User-Agent", s"$repo")
        .addHeader("Authorization", s"token $authToken")
        .setContentType("application/json", "UTF-8")
        .<<(release.asJson.noSpaces)
    }

    private def pushResourceRequest(resource: Resource,
                                    releaseId: Int): Req = {
      logger.elem(
        s"Pushing a resource to $releaseId in $owner/$repo: $resource.")
      url(s"$uploadsUrl/repos/$owner/$repo/releases/$releaseId/assets")
        .POST
        .addHeader("Accept", "application/vnd.github.v3+json")
        .addHeader("User-Agent", s"$repo")
        .addHeader("Authorization", s"token $authToken")
        .addHeader("Content-Type", resource.contentType)
        .addQueryParameter("name", resource.name)
        .addQueryParameter("label", resource.label)
        .setBody(resource.file)
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
      val handler = as.String andThen decode[ReleaseCreated]
      awaitResult(Http(pushReleaseRequest(ReleaseRequest(release)).OK(handler)))
    }

    /** Add a resource to an already-created GitHub release. */
    def pushResourceToRelease(resource: Resource, releaseId: Int): Boolean = {
      val handler = (r: Response) => r.getStatusCode == 201
      awaitResult(Http(pushResourceRequest(resource, releaseId) > handler))
    }
  }

}
