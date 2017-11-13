enablePlugins(ScalaJSPlugin)

name := "slinky-core"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value

libraryDependencies += "org.scalameta" %% "scalameta" % "1.8.0"

// TODO: use published version after there is a release with a fix for https://github.com/propensive/magnolia/issues/37
// libraryDependencies += "com.propensive" %%% "magnolia" % "0.6.0"
libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
