enablePlugins(ScalaJSPlugin)

name := "slinky"

libraryDependencies ++= {
  Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    "org.scala-js" %%% "scalajs-dom" % "0.9.1",
    "org.scalatest" %%% "scalatest" % "3.0.1" % Test,
    "com.chuusai" %%% "shapeless" % "2.3.2"
  )
}

resolvers += "WebJars" at "https://dl.bintray.com/webjars/maven/"
