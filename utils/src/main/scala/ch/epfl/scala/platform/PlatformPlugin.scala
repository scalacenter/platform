package ch.epfl.scala.platform

import com.typesafe.sbt.SbtPgp
import com.typesafe.sbt.pgp.PgpKeys
import sbtrelease.ReleasePlugin.autoImport.ReleaseStep
import xerial.sbt.Sonatype
import sbt._

object PlatformPlugin extends sbt.AutoPlugin {

  import sbtrelease.ReleasePlugin.autoImport._
  import ReleaseTransformations._
  lazy val pluginReleaseSettings = Seq(
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    releaseProcess := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      runClean,
      runTest,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      publishArtifacts,
      releaseStepTask(SbtPgp.autoImport.PgpKeys.publishSigned),
      releaseStepCommand(Sonatype.SonatypeCommand.sonatypeRelease),
      setNextVersion,
      commitNextVersion,
      pushChanges
    )
  )

}
