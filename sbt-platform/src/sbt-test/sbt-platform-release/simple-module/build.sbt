name := "foobar"

// Minimum configuration settings to release
inThisBuild(List(
  organization := "org.bitbucket.jplantdev",
  scalaVersion := "2.11.11",
  licenses := Seq("MPL-2.0" -> url("http://opensource.org/licenses/MPL-2.0")),
  developers := List(Developer("foobar", "Foo Bar",  "foobar@gmail.com", url("https://foo.bar"))),
  releaseEarlyWith := SonatypePublisher,
  releaseEarlyEnableLocalReleases := true,
  scmInfo := Some(
  ScmInfo(
    url("https://github.com/scalacenter/sbt-platform"),
    "scm:git:git@github.com:scalacenter/sbt-platform.git"
  )),
))
