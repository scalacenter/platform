package ch.epfl.scala.platform.github

import coursier.core.Version

case class GitHubRelease(version: Version,
                         body: String,
                         branch: String = "platform-release",
                         isDraft: Boolean = false,
                         preRelease: Version => Boolean = _ => false)
