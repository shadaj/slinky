enablePlugins(ScalaJSPlugin)

name := "slinky-web"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.8.1"

tpolecatDevModeOptions ~= { opts =>
  opts.filterNot(
    Set(
      ScalacOptions.privateKindProjector
    )
  )
}
