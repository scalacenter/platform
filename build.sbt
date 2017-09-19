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
    mergeDocs := {
      val logger = streams.value.log
      logger.info("Merging the docs...")
      val ornateTarget = ornateSourceDir.value
        .getOrElse(sys.error("Ornate source dir is not set."))
      IO.copyDirectory(`sbt-platform`.base / "docs", ornateTarget)
    },
    unmergeDocs := {
      val ornateTarget = ornateSourceDir.value
        .getOrElse(sys.error("Ornate source dir is not set."))
      val sbtPlatformDocs = `sbt-platform`.base / "docs"
      sbt.Path.allSubpaths(sbtPlatformDocs).foreach { t =>
        val (_, relativePath) = t
        val pathInGlobalDocs = ornateTarget / relativePath
        IO.delete(pathInGlobalDocs)
      }
    },
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
    publishProcessAndDocs := Def
      .sequential(
        mergeDocs,
        makeProcess,
        GhPagesKeys.cleanSite,
        GhPagesKeys.synchLocal,
        GhPagesKeys.pushSite,
        unmergeDocs
      )
      .value
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

val ivyScriptedCachePath = settingKey[String]("Ivy scripted cache path.")

lazy val `sbt-platform` = project
  .in(file("sbt-platform"))
  .settings(allSettings)
  .settings(ScriptedPlugin.scriptedSettings)
  .settings(
    sbtPlugin := true,
    publishMavenStyle := false,
    addSbtPlugin("com.typesafe" % "sbt-mima-plugin" % "0.1.18"),
    addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC11"),
    addSbtPlugin("me.vican.jorge" % "sbt-drone" % "0.1.1"),
    addSbtPlugin("ch.epfl.scala" % "sbt-release-early" % "1.2.0"),
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
