# Joining the Platform

> {.note}
> The following page is not complete yet.

When the Committee has incubated your project, the Process lead subscribes
you to all the Platform services. You can then benefit from Scala Center's
tools and infrastructure.

The Scala Center strives to make this process as easy as possible,
and lets you focus on the truly hard tasks: building a community,
tooling, growing your module, and fixing bugs. For that, we provide a
simple step-by-step tutorial to help you migrate.

## Continuous Integration servers

If you're using Travis, follow [this guide](http://github.com/scalaplatform/) to find out
the most straightforward way to translate the configuration logic.

> {.warning}
> From the CI web interface you can add [secrets](http://readme.drone.io/usage/secrets/)
> to store sensitive data (e.g. passwords).

> {.note}
> You don't like navigating UIs and testing your drone setup remotely? Install `drone-cli`
> and test your build logic locally with faster turnaround times. Check the [official
> guide](http://readme.drone.io/devs/cli/).

## Default module template

The [default module template](https://github.com/scalaplatform) showcases the use of the Platform infrastructure.
Check the commit history to get a feeling of the changes required to make it work.

## SBT release plugin

Our release process encourages "release early, release often" practices.
Developers and companies using older versions want to migrate and test their code as soon
as possible, and reproducible versions make it easier for them to adopt a new version.

The `platform-release` sbt plugin provides a default release pipeline that automates
*MiMa* checks, tags management, GitHub release creation, jar and release notes upload,
and synchronization with our documentation website. This default pipeline is key for the
release of *NIGHTLY*s and *BETA*s and automates the common bits that every library
release requires.

## Release bot

Once you have set up the CI and the sbt release plugin, the release bot builds, tests and
releases your module every night, following [the release model explained in the process](policies.md#release).

Remember that the release bot takes the latest changes in the `plaform-release` branch and
publishes them. Instead of using latest master, we prefer module maintainers manually push to
`platform-release` when they feel comfortable enough with the changes.
Manual intervention is deliberate, it prevents breaking compatibility and allows maintainers to
to handle versioning as they prefer.

