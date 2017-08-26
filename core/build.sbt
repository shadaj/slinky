enablePlugins(ScalaJSPlugin)

name := "slinky-core"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0"

// TODO: replace embedded source with published verison
// libraryDependencies += "com.propensive" %%% "magnolia" % "0.1.0"
