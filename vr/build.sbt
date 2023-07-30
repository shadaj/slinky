enablePlugins(ScalaJSPlugin)

name := "slinky-vr"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.16" % Test

scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
