enablePlugins(ScalaJSPlugin)

name := "slinky-vr"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8" % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

Test / scalaJSModuleKind := ModuleKind.CommonJSModule
