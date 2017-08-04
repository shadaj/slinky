enablePlugins(ScalaJSPlugin)

name := "slinky"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

// TODO: replace embedded source with published verison
// libraryDependencies += "com.propensive" %%% "magnolia" % "0.1.0"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.1" % Test
