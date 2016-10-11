lazy val buildSettings = Seq(
  organization := "ch.epfl.scala",
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

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact := true,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases" at nexus + "service/local/staging/deploy/maven2")
  },
  publishArtifact in Test := false,
  licenses := Seq(
    // Scala Center license...
    "BSD 3-Clause License" -> url(
      "http://opensource.org/licenses/BSD-3-Clause")
  ),
  homepage := Some(url("https://github.com/scalaplatform/platform")),
  autoAPIMappings := true,
  apiURL := Some(url("https://scalaplatform.github.io/platform")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/scalaplatform/platform"),
      "scm:git:git@github.com:scalaplatform/platform.git"
    )
  ),
  pomExtra :=
    <developers>
      <developer>
        <id>jvican</id>
        <name>Jorge Vicente Cantero</name>
        <url></url>
      </developer>
    </developers>
)

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {}
)

lazy val allSettings = commonSettings ++ buildSettings ++ publishSettings

lazy val platform = project
  .in(file("."))
  .settings(allSettings)
  .settings(noPublish)
  .aggregate(platformProcess)
  .dependsOn(platformProcess)

lazy val platformProcess: Project = project
  .in(file("process"))
  .enablePlugins(OrnatePlugin)
  .settings(allSettings)
  .settings(
    name := "platform-process",
    ornateTargetDir := Some(file("docs/"))
  )

lazy val customOrnate = taskKey[Unit]("customOrnate")
customOrnate in platformProcess := {
  (ornate in platformProcess).value
  // Work around Ornate limitation to add custom CSS to default theme
  val targetDir = (ornateTargetDir in platformProcess).value.get
  val cssFolder = targetDir / "_theme" / "css"
  val customCss = cssFolder / "custom.css"
  val mainCss = cssFolder / "app.css"
  IO.append(mainCss, IO.read(customCss))
}
