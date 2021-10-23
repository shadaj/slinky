enablePlugins(ScalaJSPlugin)

name := "slinky-history"

libraryDependencies += {
  if (scalaJSVersion.startsWith("0.6."))
    "org.scala-js" %%% "scalajs-dom" % "1.1.0"
  else
    "org.scala-js" %%% "scalajs-dom" % "2.0.0"
}

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}
