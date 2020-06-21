enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-scalajsreact-interop"

libraryDependencies += "com.github.japgolly.scalajs-react" %%% "core" % "1.7.1"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.2" % Test

Test / npmDependencies += "react"     -> "16.12.0"
Test / npmDependencies += "react-dom" -> "16.12.0"

Test / requireJsDomEnv := true
Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}
