enablePlugins(ScalaJSPlugin)

name := "slinky-web"

libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.6.0"

tpolecatDevModeOptions ~= { opts =>
  opts.filterNot(
    Set(
      ScalacOptions.privateKindProjector
    )
  )
}
