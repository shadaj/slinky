enablePlugins(ScalaJSPlugin)

name := "slinky-vr"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8-RC2" % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalaJSModuleKind in Test := ModuleKind.CommonJSModule
