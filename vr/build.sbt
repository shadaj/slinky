enablePlugins(ScalaJSPlugin)

name := "slinky-vr"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.6-SNAP4" % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalaJSModuleKind in Test := ModuleKind.CommonJSModule
