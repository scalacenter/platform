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

Together with [our CI](ci-integration.md), the plugin aims to provide a complete
developer experience with good error detection.

As `sbt-platform` is still *work in progress*, we're open to suggestions
on improving Scala Platform modules' maintainers. Our potential next steps are:

* Adding support for Sonatype.
* Releasing to more than one Maven/Ivy repository (Scala Platform's and your personal one).
* Extending the default release steps for every process.
* Adding release processes for release candidates and milestones.
* Adding smart test execution based on changes in git commits, i.e. instead of
running `sbt test` for all the projects, run it only for the affected subprojects.
* Improving MiMa binary compatibility checks.
* Adding source compatibility checks using Scala Meta.

If any of the previous features calls your attention, feel free to help out and make a PR.
`sbt-platform` follows the same [CONTRIBUTION](https://github.com/scalacenter/platform-staging/CONTRIBUTING.md)
guidelines than the Scala Platform process.

> {.note}
> We hope to improve the infrastructure and the maintainers support as the Scala Platform
> grows and the community gets involved. By then, we will confidently know which features
> make a real difference in maintainers' life, and will optimize for them.

## Usage

`sbt-platform` defines two main commands:

| Platform | Description |
| ------------- | ------------- |
|releaseNightly | Release a nightly version of the module. |
|releaseStable | Release a stable version of the module. |

### Examples of use
```
releaseNightly [release-version <version>] [skip-tests] [cross]
releaseStable [release-version <version>] [skip-tests] [cross]
```

All the arguments are optional and `<>` represents the parameters.

Release commands are executed by the CI. To set it up, check
the [CI documentation](sbt-platform.md).

## Setup

`sbt-platform` already comes with a default setup, thought out to get
you started as soon as possible. You only need to make sure that 
[the integration with the CI works](ci-integration.md).

However, you may find interesting changing the value of some sbt keys.
The general syntax for this is:

```scala
nameOfSbtSettingOrTask := {
  // Write here Scala code that returns a value
  // according to the type of the settings or task
}
```

### Common general settings and tasks

This section includes settings and tasks of all the sbt plugins `sbt-platform` depends on.

| Settings | Description | Type | Default |
| ------------- | ------------- | ---- | ---- |
|bintrayRepository | Name of the Bintray repository the artifact will be stored on. | `String` | N/A |
|bintrayOrganization | Name of the Bintray organization the repository is in. | `String` | `"scalaplatform"` |
|publishArtifact in Test | Setting to include the test sources in the final released artifact. | `Boolean` | `false` |

| Tasks | Description |
| ------------- | ------------- |
|bintraySyncMavenCentral | Sync bintray-published artifacts with maven central |


### Platform-specific settings

To help you get started, we provide a summary of the most common sbt tasks
and settings in `sbt-platform` and the plugins it depends on (e.g. `bintray-sbt`).

| Settings | Description | Type | Default |
| ------------- | ------------- | ---- | ---- |
|platformInsideCi | Checks if CI is executing the build. | `Boolean` | `false` |
|platformCiEnvironment | Get the Drone environment | `Option[CIEnvironment]` | `None` |
|platformReleaseOnMerge | Release on every PR merge.| `Boolean` | `false` |
|platformModuleName | Name of the module and the bintray package.| `String` | bintrayPackage.value |
|platformModuleTags | Tags for the bintray module package.| `Seq[String]` | bintrayPackageLabels.value |
|platformTargetBranch | Branch used for the platform release.| `String` | `"platform-release"` |
|platformGitHubToken | Token to publish releases to GitHub.| `String` | N/A |
|platformReleaseNotesDir | Directory with the markdown release notes.| `String` | `baseDirectory.value / "notes"` |
|platformSignArtifact | Enable to sign artifacts with the platform pgp key.| `Boolean` | `true` |
|platformPgpRings | Files that store the pgp public and secret ring respectively.| `Option[(File, File)]`| N/A |
|platformBeforePublishHook | A hook to customize all the release processes before publishing to Bintray.| `Task[Unit]` | N/A |
|platformAfterPublishHook | A hook to customize all the release processes after publishing to Bintray.| `Task[Unit]`| N/A |

### Platform-specific tasks

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
|platformActiveReleaseProcess | The active release process if `releaseNightly` or `releaseStable` has been executed.|
|platformNightlyReleaseProcess | The default nightly release process.|
|platformStableReleaseProcess | The default stable release process.|

## Release processes

The release process setup relies on [sbt-release](https://github.com/sbt/sbt-release)
and follows the [Scala Platform release process](https://github.com/scalacenter/platform-staging).
  
`sbt-platform` defines two default release processes for stable and nightly versions,
if you want to add a new one or modify the standard ones, go [here](#modify-release-process).

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

To cut a stable release, tag the `platform-release` branch with the appropiate
version number and push. When the CI is finished, your release will be available
in [modules-releases](https://bintray.com/scalaplatform/modules-releases).

#### Decide the version number

You're free to choose the version number of the library as long as the bumped number
is consistent with the Scala Platform version your module is targeting.
If the latest Scala Platform release was `1.2` and the next one
is `1.3`, then your stable version is only allowed a minor bump (e.g. `2.3` => `2.4`, `1.1` => `1.4`).

### Modify the release process {#modify-release-process}

If you want to add custom release steps, define either `platformBeforePublishHook`
or `platformAfterPublishHook`. As an example, let's register
a new GitHub release for every nightly (disabled by default).

```scala
platformAfterPublishHook := {
    val logger = streams.value.log
    val active = platformActiveReleaseProcess.value  
    if (active.exists(platformNightlyReleaseProcess.value)) {
      logger.info("Cool! Nightlies are now released to GitHub.")
      platformReleaseToGitHub.value
    }
}
```

Every time you execute the nightly release process, the release process
creates a release in GitHub and adds your markdown notes under `/notes`
as release notes (if any).

### On further extensions
`sbt-release` provides primitives to write release processes from scratch:

1. `releaseStepTask` - Run an individual task. Does not aggregate builds.
1. `releaseStepTaskAggregated` - Run an aggregated task.
1. `releaseStepInputTask` - Run an input task, optionally taking the input to pass to it.
1. `releaseStepCommand` - Run a command.

Use them if you want to extend the release process with input tasks, or any
other feature that requires more fine-grained control.
When you are finished, set your new release process to `platformNightlyReleaseProcess` or
`platformStableReleaseProcess`.
  
> {.note}
> `sbt-platform` does not provide good support for advanced use cases yet.
> We strongly recommend you to check the authoritative `sbt-release` [guide](https://github.com/sbt/sbt-release)
> to learn how to extend the release process. As a plus, you can always
> check `sbt-platform`'s [source code](https://github.com/scalacenter/platform-staging/sbt-platform/src/main/scala/ch/epfl/scala/platform/PlatformPlugin.scala).
