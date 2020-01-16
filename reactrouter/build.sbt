enablePlugins(ScalaJSPlugin)

name := "slinky-react-router"

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}

