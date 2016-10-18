import sbt._
import Keys._
import sbtrelease.Utilities._
import sbtrelease.ReleasePlugin.autoImport._

object CustomReleaseSteps {
  lazy val customPublishArtifacts = ReleaseStep(
    action = customRunPublishArtifactAction,
    check = st => {
      // getPublishTo fails if no publish repository is set up.
      val ex = st.extract
      val ref = ex.get(thisProjectRef)
      Classpaths.getPublishTo(ex.get(publishTo in Global in ref))
      st
    },
    enableCrossBuild = true
  )

  private lazy val customRunPublishArtifactAction = { st: State =>
    val extracted = st.extract
    val ref = extracted.get(thisProjectRef)
    println(ref)
    println(extracted.get(publishMavenStyle))
    println(extracted.get(publishArtifact))
    println(extracted.get(publishConfiguration))
    println(publish in Global in ref)
    println(st.definedCommands)
    println(st.attributes)
    extracted.runAggregated(publish in Global in ref, st)
  }
}
