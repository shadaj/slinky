enablePlugins(ScalaJSPlugin)

name := "slinky-history"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.7"

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}
