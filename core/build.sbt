enablePlugins(ScalaJSPlugin)

name := "simple-react"

jsDependencies += RuntimeDOM

libraryDependencies ++= {
  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "org.scalatest" %%% "scalatest" % "3.0.1" % Test
  )
}

resolvers += "WebJars" at "https://dl.bintray.com/webjars/maven/"
