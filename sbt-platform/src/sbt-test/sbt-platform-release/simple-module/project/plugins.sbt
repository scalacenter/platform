// Ideal for local changes.
val PlatformBuild = RootProject(file(sys.props("sourcedep.basedir")))
dependsOn(ProjectRef(PlatformBuild.build, sys.props("sourcedep.name")))
