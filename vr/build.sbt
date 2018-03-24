enablePlugins(ScalaJSPlugin)

name := "slinky-vr"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.3" % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
scalacOptions += "-Ywarn-unused-import"

scalaJSModuleKind in Test := ModuleKind.CommonJSModule
