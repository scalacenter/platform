# Automatic release manager
[![Build Status](http://stats.lassie.io:8001/api/badges/jvican/platform-staging/status.svg)](http://stats.lassie.io:8001/jvican/platform-staging)

The release manager takes care of reading a file of modules, cloning them,
checking out the `platform-release` branches (configurable) and executing
the default release pipeline, for nightlies, betas and stables, provided
by `sbt-platform`. It only runs in JDK8.