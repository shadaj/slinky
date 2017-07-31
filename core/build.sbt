enablePlugins(ScalaJSPlugin)

name := "slinky"

libraryDependencies += "org.scala-lang" % "scala-reflect" % scalaVersion.value
libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"

// TODO: replace embedded source with published verison
// libraryDependencies += "com.propensive" %%% "magnolia" % "0.1.0"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.1" % Test

resolvers += "WebJars" at "https://dl.bintray.com/webjars/maven/"
