# Automatic release manager
[![Build Status](http://stats.lassie.io:8001/api/badges/scalacenter/platform-staging/status.svg)](http://stats.lassie.io:8001/scalacenter/platform-staging)

The release manager takes care of reading a file of modules, cloning them,
checking out the `platform-release` branches (configurable) and executing
the default release pipeline, for nightlies, betas and stables, provided
by `sbt-platform`. It only runs in JDK8.

Just run the release manager:
> sbt> run release-manager/src/main/resources/MODULES-TEST.toml

In the future, this tool will be synced up with Scala Center infrastructure
and executed every night, according to the rules defined in the Platform
release process.
