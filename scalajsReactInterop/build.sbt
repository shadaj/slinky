enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-scalajsreact-interop"

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) =>
      Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % "1.7.7"
    )
    case _ =>
      Seq(
      "com.github.japgolly.scalajs-react" %%% "core" % "2.0.0-RC1"
    )
  }
}

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.9" % Test

Test / npmDependencies += "react"     -> "16.12.0"
Test / npmDependencies += "react-dom" -> "16.12.0"

Test / requireJsDomEnv := true
Test / jsEnv := new org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv()

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}
