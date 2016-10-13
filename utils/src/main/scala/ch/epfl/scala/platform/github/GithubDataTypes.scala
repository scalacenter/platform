package ch.epfl.scala.platform.github

trait GithubDataTypes {
  case class ReleaseCreated(
      url: String,
      html_url: String,
      assets_url: String,
      upload_url: String,
      tarball_url: String,
      zipball_url: String,
      id: Int,
      tag_name: String,
      target_commitish: String,
      name: String,
      body: String,
      draft: String,
      prerelease: String,
      created_at: String,
      published_at: String,
      author: AuthorInfo,
      assets: List[Asset]
  )

  case class Asset(
      url: String,
      browser_download_url: String,
      id: Int,
      name: String,
      label: String,
      state: String,
      content_type: String,
      size: Int,
      download_count: Int,
      created_at: String,
      updated_at: String,
      uploader: AuthorInfo
  )

  case class AuthorInfo(
      login: String,
      id: Int,
      avatar_url: String,
      gravatar_id: String,
      url: String,
      html_url: String,
      followers_url: String,
      following_url: String,
      gists_url: String,
      starred_url: String,
      subscriptions_url: String,
      organizations_url: String,
      repos_url: String,
      events_url: String,
      received_events_url: String,
      `type`: String,
      site_admin: Boolean
  )
}
