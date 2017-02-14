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

jsDependencies += "org.webjars.bower" % "react" % "15.4.1" / "react-with-addons.js" minified "react-with-addons.min.js" commonJSName "React"
jsDependencies += "org.webjars.bower" % "react" % "15.4.1" / "react-dom.js" minified "react-dom.min.js" dependsOn "react-with-addons.js" commonJSName "ReactDOM"
