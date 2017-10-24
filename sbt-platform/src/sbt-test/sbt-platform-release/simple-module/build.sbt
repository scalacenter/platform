// Minimum configuration settings to release
inThisBuild(List(
  organization := "org.bitbucket.jplantdev",
  scalaVersion := "2.12.3",
  licenses := Seq("MPL-2.0" -> url("http://opensource.org/licenses/MPL-2.0")),
  developers := List(Developer("foobar", "Foo Bar",  "foobar@gmail.com", url("https://foo.bar"))),
  // Only necessary for scripted
  releaseEarlyEnableLocalReleases := true,
  scmInfo := Some(
  ScmInfo(
    url("https://github.com/scalacenter/sbt-platform"),
    "scm:git:git@github.com:scalacenter/sbt-platform.git"
  )),
))

val foobar = project.in(file("."))
