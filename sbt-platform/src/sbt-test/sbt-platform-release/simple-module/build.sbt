name := "stoml"

// Minimum configuration settings to release
inScope(Scope.GlobalScope)(List(
  organization := "me.vican.jorge",
  scalaVersion := "2.11.11",
  licenses := Seq("MPL-2.0" -> url("http://opensource.org/licenses/MPL-2.0")),
  developers := List(Developer("foobar", "Foo Bar",  "foobar@gmail.com", url("https://foo.bar"))),
))
