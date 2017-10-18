addSbtPlugin("ch.epfl.scala" % "sbt-release-early" % "2.0.0")
addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value
