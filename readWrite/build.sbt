enablePlugins(ScalaJSPlugin)

name := "slinky-readwrite"

// TODO: use published version after there is a release with support for fallback derivations
// libraryDependencies += "com.propensive" %%% "magnolia" % "0.6.0"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "org.scala-lang" % "scala-compiler" % scalaVersion.value
