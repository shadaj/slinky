enablePlugins(ScalaJSPlugin)

name := "slinky-vr"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.12" % Test

scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
