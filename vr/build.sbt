enablePlugins(ScalaJSPlugin)
enablePlugins(SlinkyPlugin)

name := "slinky-vr"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.18" % Test

scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
