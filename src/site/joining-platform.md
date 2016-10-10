# Joining the Platform [WIP]

> {.note}
> The following page is not complete and should not be reviewed. There
> is a lot missing and its structure will change.

When the Committee has incubated your project, the Process lead subscribes
you to all the Platform services and you start benefiting from its tools
and infrastructure.

The Scala Center strives to make this process as easy as possible,
and let you focus on the truly hard tasks: building a community,
tooling, growing your module, and fixing bugs. For that, we provide a
simple step-by-step tutorial to help you migrate.

## Continuous Integration servers

Our Drone servers provide quick development experience to module maintainers.
With 48 cores and 64 gigabytes of memory, we ensure that PRs are built fast and that the
turnaround is short.

Setting up the CI is just one link away. Go to [our Drone setup](http://stats.lassie.io:8001),
log in and enable your project. Then, push a `drone.yml` file with the build logic
and the CI will kick in. From the CI web interface you can add [secrets](http://readme.drone.io/usage/secrets/)
to store sensitive data (e.g. passwords).

## Default module template

The [default module template](https://github.com/scalaplatform) showcases the use of the Platform infrastructure.

## SBT release plugin

Releasing *reproducible* versions often is important. Developers and companies that
need to migrate their code want to test your changes as soon as possible, and prepare
for its future adoption.

The `platform-release` sbt plugin provides a default release pipeline that automates
*MiMa* checks, tags management, GitHub release creation, jar upload. This default
pipeline is key for the release of *NIGHTLY*s and *BETA*s.

## Release bot

Once you have set up the CI and the sbt release plugin, the release bot builds, tests and
releases your module every night, following [the release model explained in the process](policies.md#release).
