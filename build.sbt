lazy val publishSettings = Seq(
  publishMavenStyle := true,
  bintrayOrganization := Some("scalaplatform"),
  bintrayRepository := "tools",
  bintrayPackageLabels := Seq("scala", "platform", "tools", "sbt"),
  publishTo := (publishTo in bintray).value,
  publishArtifact in Test := false,
  licenses := Seq(
    // Scala Center license... BSD 3-clause
    "BSD" -> url("http://opensource.org/licenses/BSD-3-Clause")
  ),
  homepage := Some(url("https://github.com/scalaplatform/platform")),
  autoAPIMappings := true,
  apiURL := Some(url("https://scalaplatform.github.io/platform")),
  pomExtra :=
    <developers>
      <developer>
        <id>jvican</id>
        <name>Jorge Vicente Cantero</name>
        <url></url>
      </developer>
    </developers>
)
lazy val buildSettings = Seq(
  organization := "ch.epfl.scala",
  resolvers += Resolver.jcenterRepo,
  resolvers += Resolver.bintrayRepo("jvican", "releases"),
  resolvers += Resolver.bintrayRepo("scalaplatform", "tools"),
  updateOptions := updateOptions.value.withCachedResolution(true)
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
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "junit" % "junit" % "4.12" % "test"
)

lazy val noPublish = Seq(
  publishArtifact := false,
  publish := {},
  publishLocal := {}
)

lazy val allSettings = commonSettings ++ buildSettings ++ publishSettings

lazy val platform = project
  .in(file("."))
  .settings(allSettings)
  .settings(noPublish)
  .settings(scalaVersion := "2.11.8")
  .aggregate(process, `release-manager`)
  .dependsOn(process, `release-manager`)

lazy val mergeDocs = taskKey[Unit]("Merge Process and `sbt-platform docs.")
lazy val makeProcess = taskKey[Unit]("Make the process.")
lazy val createProcessIndex = taskKey[Unit]("Create index.html.")
lazy val publishProcessAndDocs = taskKey[Unit]("Make and publish the process.")
lazy val unmergeDocs =
  taskKey[Unit]("Remote the `sbt-platform` docs from the source folder.")
lazy val process: Project = project
  .in(file("process"))
  .settings(allSettings)
  .settings(scalaVersion := "2.11.8")
  .settings(name := "platform-process")

lazy val `release-manager` = project
  .in(file("release-manager"))
  .settings(allSettings)
  .settings(scalaVersion := "2.11.8")
  .settings(
    libraryDependencies ++= Seq(
      "org.eclipse.jgit" % "org.eclipse.jgit" % "4.5.0.201609210915-r",
      "me.vican.jorge" %% "stoml" % "0.2",
      "org.typelevel" %% "cats" % "0.7.2"
    ) ++ testDependencies
  )

lazy val `sbt-platform` = project
  .in(file("sbt-platform"))
  .settings(allSettings)
  .settings(
    sbtPlugin := true,
    publishMavenStyle := false,
    addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.18"),
    addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12"),
    addSbtPlugin("ch.epfl.scala" % "sbt-release-early" % "2.0.0"),
    addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "0.9.3"),
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
