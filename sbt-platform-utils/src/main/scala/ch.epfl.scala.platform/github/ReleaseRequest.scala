package ch.epfl.scala.platform.github

import ch.epfl.scala.platform

case class ReleaseRequest(tag_name: String,
                          target_commitish: String,
                          name: String,
                          body: String,
                          draft: Boolean,
                          prerelease: Boolean)

object ReleaseRequest {
  def apply(release: GitHubRelease): ReleaseRequest = {
    val versionNumber = s"v${release.version.repr}"
    ReleaseRequest(versionNumber,
                   release.branch,
                   versionNumber,
                   release.body,
                   release.isDraft || platform.testing,
                   release.preRelease(release.version))
  }
}
