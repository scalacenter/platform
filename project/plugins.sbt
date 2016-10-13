logLevel := Level.Warn
resolvers += Classpaths.sbtPluginReleases

addSbtPlugin("com.novocode" % "sbt-ornate" % "0.2")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "1.1")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.3")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
