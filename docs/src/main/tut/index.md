---
layout: home
title:  "Home"
section: "home"
technologies:
 - first: ["Scala", "sbt-microsites plugin is completely written in Scala"]
 - second: ["SBT", "sbt-microsites plugin uses SBT and other sbt plugins to generate microsites easily"]
---

# sbt-platform

**sbt-platform** is a SBT plugin that aims at simplifying the maintenance
and release of Scala Platform modules. It automates common use cases to
speed up developers' workflow and let module maintainers focus on the essential.

Add it to your project (`project/plugins.sbt`) with:
```
addSbtPlugin("ch.epfl.scala" % "sbt-platform" % "0.1")
```

The `sbt-platform` plugin provides a tight integration with the Scala Platform CI
server and infrastructure. For now, it covers the following use cases:

* [Scala Platform policies](https://scalacenter.github.io/platform-staging).
* Automatic release of nightlies.
* Automatic release of stable versions when a git tag is found.
* MiMa compatibility checks and PGP signatures for artifacts (with Scala Platform keys).
* Integration with our Drone setup for writing your own sbt scripts.
* Automatic detection of cumbersome sbt keys (e.g. `scmInfo`).
* Automatic configuration of common sbt plugins:
    * `sbt-bintray`
    * `sbt-pgp`
    * `sbt-release`
    * `sbt-mima-plugin`
    * `sbt-coursier`

Together with our CI, that provides a default cached Scala and SBT setup, the 
plugin aims to provide a complete developer experience with
good error detection.

As `sbt-platform` is still *work in progress*, we're open to suggestions
on improving Scala Platform modules' maintainers. Potential next steps are:

* Adding support for Sonatype.
* Extending the default release steps for every process.
* Adding release processes for release candidates and milestones.
* Adding smart test execution based on changes in git commits, i.e. instead of
running `sbt test` for all the projects, run it only for the affected subprojects.
* Improving MiMa binary compatibility checks.
* Adding source compatibility checks using Scala Meta.

## Usage

`sbt-platform` defines two main commands:

| Platform | Description |
| ------------- | ------------- |
|releaseNightly | Release a nightly version of the module. |
|releaseStable | Release a stable version of the module. |

Release commands are executed by the CI. To set it up, check
the [CI documentation](wip).

## Keys

Here are some sbt keys you may be interested in setting up.

| SBT keys | Description |
| ------------- | ------------- |
|platformInsideCi | Checks if CI is executing the build. |
|platformCiEnvironment | Get the Drone environment |
|sonatypeUsername | Get sonatype username.|
|sonatypePassword | Get sonatype password.|
|platformReleaseOnMerge | Release on every PR merge.|
|platformModuleTags | Tags for the bintray module package.|
|platformTargetBranch | Branch used for the platform release.|
|platformValidatePomData | Ensure that all the data is available before generating a POM file.|
|platformScalaModule | Create the ScalaModule from the basic assert info.|
|platformSbtDefinedVersion | Get the sbt-defined version of the current module.|
|platformCurrentVersion | Get the current version used for releases.|
|platformLatestPublishedVersion | Fetch latest published stable version.|
|platformNextVersionFun | Function that decides the next version.|
|platformRunMiMa | Run MiMa and report results based on current version.|
|platformGitHubToken | Token to publish releses to GitHub.|
|platformReleaseNotesDir | Directory with the markdown release notes.|
|platformGetReleaseNotes | Get the correct release notes for a release.|
|platformReleaseToGitHub | Create a release in GitHub.|
|platformGitHubRepo | Get GitHub organization and repository from .git folder.|
|platformNightlyReleaseProcess | The nightly release process for a Platform module.|
|platformStableReleaseProcess | The nightly release process for a Platform module.|
|platformSignArtifact | Enable to sign artifacts with the platform pgp key.|
|platformCustomRings | File that stores the pgp secret ring.|
|platformDefaultPublicRingName | Default file name for fetching the public gpg keys.|
|platformDefaultPrivateRingName | Default file name for fetching the private gpg keys.|
|platformBeforePublishHook | A release hook to customize the beginning of the release process.|
|platformAfterPublishHook | A release hook to customize the end of the release process.|
