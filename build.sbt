lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  // Scala Center license... BSD 3-clause
  licenses := Seq("BSD" -> url("http://opensource.org/licenses/BSD-3-Clause")),
  homepage := Some(url("https://github.com/scalaplatform/platform")),
  developers += Developer("jvican",
                          "Jorge Vicente Cantero",
                          "jorge@vican.me",
                          url("https://jorge.vican.me"))
)

inThisBuild(
  Seq(
    organization := "org.scala-lang.platform",
    resolvers += Resolver.jcenterRepo,
    resolvers += Resolver.bintrayRepo("jvican", "releases"),
    resolvers += Resolver.bintrayRepo("scalaplatform", "tools"),
    updateOptions := updateOptions.value.withCachedResolution(true)
  )
)

lazy val compilerOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-unchecked",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture",
  "-Xlint"
)

lazy val commonSettings = Seq(
  triggeredMessage in ThisBuild := Watched.clearWhenTriggered,
  watchSources += baseDirectory.value / "resources",
  scalacOptions in (Compile, console) := compilerOptions,
  testOptions in Test += Tests.Argument("-oD")
)

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "junit" % "junit" % "4.12" % "test"
)

lazy val noPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val allSettings = commonSettings ++ publishSettings

lazy val platform = project
  .in(file("."))
  .settings(allSettings)
  .settings(noPublish)
  .settings(scalaVersion := "2.12.3")
  .aggregate(process, `release-manager`)
  .dependsOn(process, `release-manager`)

lazy val process: Project = project
  .in(file("process"))
  .settings(allSettings)
  .settings(scalaVersion := "2.12.3")
  .settings(name := "platform-process")

lazy val `release-manager` = project
  .in(file("release-manager"))
  .settings(allSettings)
  .settings(scalaVersion := "2.12.3")
  .settings(
    libraryDependencies ++= Seq(
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.5.0.201609210915-r",
      "me.vican.jorge" %% "stoml" % "0.4",
      "org.typelevel" %% "cats" % "0.8.1"
    ) ++ testDependencies
  )

lazy val `sbt-platform` = project
  .in(file("sbt-platform"))
  .enablePlugins(ScriptedPlugin)
  .settings(allSettings)
  .settings(
    sbtPlugin := true,
    scalaVersion := "2.12.3",
    publishMavenStyle := false,
    addSbtPlugin(
      ("ohnosequences" % "sbt-github-release" % "0.5.1").classifier("fat").intransitive()),
    addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.18"),
    addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12"),
    addSbtPlugin("ch.epfl.scala" % "sbt-release-early" % "2.0.0"),
    addSbtPlugin("com.thoughtworks.sbt-api-mappings" % "sbt-api-mappings" % "2.0.0"),
    libraryDependencies ++= testDependencies,
    scriptedLaunchOpts := Seq(
      "-Dplugin.version=" + version.value,
      "-Xmx1g",
      "-Xss16m",
      "-Dplatform.debug=true",
      "-Dplatform.test=true"
    ) ++ {
      // Pass along custom boot properties if specified
      val bootProps = "sbt.boot.properties"
      sys.props.get(bootProps).map(x => s"-D$bootProps=$x").toList
    },
    scriptedBufferLog := false,
    javaOptions in Test ++= Seq("-Dplatform.debug=true", "-Dplatform.test=true")
  )
