logLevel := Level.Warn
resolvers += Classpaths.sbtPluginReleases
resolvers += Resolver.bintrayIvyRepo("scalaplatform", "tools")

addSbtPlugin("com.novocode" % "sbt-ornate" % "0.2")
addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0")
addSbtPlugin("ch.epfl.scala" % "sbt-release" % "1.0.6")
// Disable temporarily to resolve sbt plugins
//addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-M14")

libraryDependencies += "org.scala-sbt" % "scripted-plugin" % sbtVersion.value
