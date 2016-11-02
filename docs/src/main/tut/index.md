---
layout: home
title:  "Home"
section: "home"
technologies:
 - first: ["Scala", "sbt-platform plugin is completely written in Scala"]
 - second: ["SBT", "sbt-platform plugin uses SBT and other sbt plugins to simplify modules maintainers' life"]
 - third: ["Scala Center", "sbt-platform plugin is a Scala Center initiative for the good of all the Scala community"]
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
* Releasing to more than one Maven/Ivy repository (Scala Platform's and your personal one).
* Extending the default release steps for every process.
* Adding release processes for release candidates and milestones.
* Adding smart test execution based on changes in git commits, i.e. instead of
running `sbt test` for all the projects, run it only for the affected subprojects.
* Improving MiMa binary compatibility checks.
* Adding source compatibility checks using Scala Meta.

Note that while this is an active Scala Center project,
we hope to evolve the infrastructure as the Scala Platform grows and the community
gets involved.

If any of the previous features calls your attention, feel free to help out and make a PR.
`sbt-platform` follows the same [CONTRIBUTION]() process (C4) and guidelines specified in the
Scala Platform process.

## Usage

`sbt-platform` defines two main commands:

| Platform | Description |
| ------------- | ------------- |
|releaseNightly | Release a nightly version of the module. |
|releaseStable | Release a stable version of the module. |

### Examples of use
```scala
releaseNightly()
```

Release commands are executed by the CI. To set it up, check
the [CI documentation](wip).

## Configuring your setup

Understanding the most important keys for your sbt build is essential.

To help you get started, we provide a summary of the most common sbt tasks
and settings in `sbt-platform` and the plugins it depends on (e.g. `bintray-sbt`).

| Settings | Description | Type | Default |
| ------------- | ------------- | ---- | ---- |
|platformInsideCi | Checks if CI is executing the build. | `Boolean` | `false` |
|platformCiEnvironment | Get the Drone environment | `Option[CIEnvironment]` | `None` |
|platformReleaseOnMerge | Release on every PR merge.| `Boolean` | `false` |
|platformModuleName | Name of the module and the bintray package.| `String` | N/A|
|platformModuleTags | Tags for the bintray module package.| `Seq[String]` | `Seq.empty[String]` |
|platformTargetBranch | Branch used for the platform release.| `String` | `"platform-release"` |
|platformGitHubToken | Token to publish releases to GitHub.| `String` | N/A |
|platformReleaseNotesDir | Directory with the markdown release notes.| `String` | `baseDirectory.value / "notes"` |
|platformSignArtifact | Enable to sign artifacts with the platform pgp key.| `Boolean` | `true` |
|platformPgpRings | Files that store the pgp public and secret ring respectively.| `Option[(File, File)]`| N/A |
|platformBeforePublishHook | A hook to customize all the release processes before publishing to Bintray.| `Task[Unit]` | N/A |
|platformAfterPublishHook | A hook to customize all the release processes after publishing to Bintray.| `Task[Unit]`| N/A |

| Tasks | Description |
| ------------- | ------------- |
|platformValidatePomData | Ensure that all the data is available before generating a POM file.|
|platformCurrentVersion | Get the current version to be released.|
|platformLatestPublishedVersion | Fetch latest published stable version.|
|platformPreviousArtifacts | Get `mimaPreviousArtifacts` or fetch latest artifact to run MiMa. |
|platformNextVersionFun | Function that decides the next version.|
|platformRunMiMa | Run MiMa and report results based on current version.|
|platformGetReleaseNotes | Get the correct release notes for a release.|
|platformReleaseToGitHub | Create a release in GitHub.|
|platformNightlyReleaseProcess | The default nightly release process.|
|platformStableReleaseProcess | The default stable release process.|

## Release processes

The release process setup relies on [sbt-release](https://github.com/sbt/sbt-release)
and follows the [Scala Platform release process](https://github.com/scalacenter/platform-staging).
  
`sbt-platform` defines two default release processes for stable and nightly versions,
if you want to add a new one or modify the standard ones, go [here](modify-release-process).

### Platform Bintray repositories

The Scala Platform uses Bintray to store and release all the Scala modules artifacts. Bintray allows
maintainers a fine-grained control over their releases and provides instant release time and synchronization
with Maven Central. The [Bintray Scala Platform](https://bintray.com/scalaplatform) organization owns repositories:

1. [modules-releases](https://bintray.com/scalaplatform/modules-releases) - Stable releases (and, in the future, milestones and RCs).
1. [modules-nightly-releases](https://bintray.com/scalaplatform/modules-nightly-releases) - Nightly releases.
1. [tools](https://bintray.com/scalaplatform/tools) - Scala Platform maintainers' tools.

### How nightlies work

The Scala Platform bot invokes `releaseNightly` daily at 00:00. The nightly release process
will warn you when binary compatibility is broken, the version number is invalid, tests don't
pass or the release of the artifact to Bintray has failed.

### Stable releases

When you're ready for a stable release, tag the `platform-release` branch with the appropiate
version number.

In order to decide the version number, look at the bump between the latest Scala Platform
version and the next one. If the latest Scala Platform release was `1.2` and the next one
is `1.3`, then your stable version can have one minor bump regarding your latest release (`2.3` => `2.4`).

### Modifying a release process
<a name="modify-release-process"></a>

If you want to add custom release steps, you can define either `platformBeforePublishHook`
or `platformAfterPublishHook` to perform an extra task for you. In this case, we'll register
a new GitHub release for every nightly (disabled by default).

```scala
platformAfterPublishHook := {
    val logger = streams.value.log
    logger.info("Cool! Nightlies are now released to GitHub.")
    releaseStepTask(platformReleaseToGitHub)
}
```

In the previous snippet, `releaseStepTask(platformReleaseToGitHub)` allows you to reuse
the already defined task that [creates a release in GitHub](https://github.com/scalaplatform/dummy/releases/tag/untagged-ac904793e0df7da84fa6).
If you want to extend your example with fancier tasks and commands, have a look at the following
`sbt-release` helpers:

1. `releaseStepTask` - Run an individual task. Does not aggregate builds.
1. `releaseStepTaskAggregated` - Run an aggregated task.
1. `releaseStepInputTask` - Run an input task, optionally taking the input to pass to it.
1. `releaseStepCommand` - Run a command.

If you want to perform major changes in the nightly release process, you can set or
modify the release process task with `platformNightlyReleaseProcess` or `platformStableReleaseProcess`.
For more information about the release steps that you can and cannot remove,
check the [source code](https://github.com/scalacenter/platform-staging/sbt-platform/src/main/scala/ch/epfl/scala/platform/PlatformPlugin.scala).

We strongly recommend to check the authoritative `sbt-release` [guide](https://github.com/sbt/sbt-release) to learn how to
extend the release process.
  
**NOTE**: Although you can set up `sbt-release` settings and tasks, because `sbt-platform` relies
on them, you should never invoke `release` by yourself unless you really know what you're doing.
Use either `releaseStable` or `releaseNightly`.

