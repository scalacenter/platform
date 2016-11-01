//import com.typesafe.sbt.pgp.PgpKeys
//import sbtrelease.ReleaseStateTransformations._

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  /* bintrayOrganization := Some("scalaplatform"),
    bintrayRepository := "tools",
    bintrayPackageLabels := Seq("scala", "platform", "tools", "sbt"),
    publishTo := (publishTo in bintray).value,
    publishArtifact in Test := false,
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,*/
  licenses := Seq(
    // Scala Center license... BSD 3-clause
    "BSD" -> url("http://opensource.org/licenses/BSD-3-Clause")
  ),
  homepage := Some(url("https://github.com/scalaplatform/platform")),
  autoAPIMappings := true,
  apiURL := Some(url("https://scalaplatform.github.io/platform")),
  /*  releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),*/
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
  scalacOptions in(Compile, console) := compilerOptions,
  testOptions in Test += Tests.Argument("-oD")
)

lazy val testDependencies = Seq(
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "junit" % "junit" % "4.12" % "test"
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
  .settings(scalaVersion := "2.11.8")
  .aggregate(process, `release-manager`)
  .dependsOn(process, `release-manager`)

lazy val makeProcess = taskKey[Unit]("Make the process.")
lazy val createProcessIndex = taskKey[Unit]("Create index.html.")
lazy val publishProcess = taskKey[Unit]("Make and publish the process.")
lazy val process: Project = project
  .in(file("process"))
  .enablePlugins(OrnatePlugin)
  .settings(allSettings)
  .settings(scalaVersion := "2.11.8")
  .settings(
    ghpages.settings,
    git.remoteRepo := "git@github.com:scalacenter/platform-staging",
    name := "platform-process",
    ornateSourceDir := Some(baseDirectory.value / "src" / "ornate"),
    ornateTargetDir := Some(target.value / "site"),
    siteSourceDirectory := ornateTargetDir.value.get,
    makeProcess := {
      val logger = streams.value.log
      ornate.value
      // Work around Ornate limitation to add custom CSS
      val targetDir = ornateTargetDir.value.get
      val cssFolder = targetDir / "_theme" / "css"
      if (!cssFolder.exists) cssFolder.mkdirs()
      val processDir = baseDirectory.value
      val resourcesFolder = processDir / "src" / "resources"
      val customCss = resourcesFolder / "css" / "custom.css"
      val mainCss = cssFolder / "app.css"
      logger.info("Adding custom CSS...")
      IO.append(mainCss, IO.read(customCss))
    },
    createProcessIndex := {
      val logger = streams.value.log
      // Redirecting index to contents...
      val repositoryTarget = GhPagesKeys.repository.value
      import java.nio.file.{Paths, Files}
      def getPath(f: java.io.File): java.nio.file.Path =
        Paths.get(f.toPath.toAbsolutePath.toString)
      val parentPath = getPath(repositoryTarget)
      val destFile = getPath(repositoryTarget / "index.html")
      logger.info(s"Checking that $destFile does not exist.")
      if (!Files.isSymbolicLink(destFile)) {
        val srcLink = Paths.get("contents.html")
        logger.info(s"Generating index.html poiting to $srcLink.")
        Files.createSymbolicLink(destFile, srcLink)
      }
    },
    GhPagesKeys.synchLocal :=
      GhPagesKeys.synchLocal.dependsOn(createProcessIndex).value,
    publishProcess := Def.sequential(
      makeProcess,
      GhPagesKeys.cleanSite,
      GhPagesKeys.synchLocal,
      GhPagesKeys.pushSite
    ).value
  )

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

val circeVersion = "0.5.1"
val ivyScriptedCachePath = settingKey[String]("Ivy scripted cache path.")
lazy val `sbt-platform` = project
  .in(file("sbt-platform"))
  .settings(allSettings)
  .settings(ScriptedPlugin.scriptedSettings)
  .settings(
    sbtPlugin := true,
    publishMavenStyle := false,
    addSbtPlugin("me.lessis" % "bintray-sbt" % "0.3.0"),
    addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.0.0"),
    addSbtPlugin("ch.epfl.scala" % "sbt-release" % "1.0.7"),
    addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.11"),
    addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-M14"),
    addCompilerPlugin(
      "org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full
    ),
    libraryDependencies ++= Seq(
      "com.eed3si9n" %% "gigahorse-core" % "0.1.1",
      "com.lihaoyi" %% "sourcecode" % "0.1.1",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      // Magically solves NoSuchMethodError, bintry depends on it, sigh
      "net.databinder.dispatch" %% "dispatch-json4s-native" % "0.11.3",
      "com.github.nscala-time" %% "nscala-time" % "2.14.0"
    ) ++ testDependencies,
    // Solves binary incompatible library mix...
    dependencyOverrides += "org.json4s" %% "json4s-core" % "3.2.10",
    dependencyOverrides += "org.json4s" %% "json4s-native" % "3.2.10",
    dependencyOverrides += "org.json4s" %% "json4s-ast" % "3.2.10",
    ivyScriptedCachePath := {
      if (sys.env.get("CI").exists(_.toBoolean))
        "-Dsbt.ivy.home=/drone/.ivy2"
      else s"-Dsbt.ivy.home=${ivyPaths.value.ivyHome.get}"
    },
    scriptedLaunchOpts := Seq(
      "-Dplugin.version=" + version.value,
      // .jvmopts is ignored, simulate here
      "-XX:MaxPermSize=256m",
      "-Xmx2g",
      "-Xss2m",
      ivyScriptedCachePath.value,
      "-Dplatform.debug=true",
      "-Dplatform.test=true"
    ),
    scriptedBufferLog := false,
    fork in Test := true,
    javaOptions in Test ++= Seq("-Dplatform.debug=true",
      "-Dplatform.test=true")
  )

lazy val docs = project
  .in(file("docs"))
  .settings(moduleName := "sbt-platform-docs")
  .enablePlugins(MicrositesPlugin)
  .settings(allSettings)
  .settings(
    micrositeName := "sbt-platform",
    micrositeDescription := "The SBT plugin for Scala Platform modules' maintainers.",
    micrositeBaseUrl := "/sbt-platform",
    micrositeAuthor := "Scala Center",
    micrositeHomepage := "scala.epfl.ch",
    micrositeGithubOwner := "scalacenter",
    micrositeGithubRepo := "platform-staging",
    siteDirectory in makeSite := target(_ / "site" / "sbt-platform").value,
    git.remoteRepo := "git@github.com:scalacenter/sbt-platform",
    git.branch := Some("master")
  )
