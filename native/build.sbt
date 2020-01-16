enablePlugins(ScalaJSPlugin)

import org.scalajs.jsenv.jsdomnodejs.JSDOMNodeJSEnv

import scala.util.Properties

name := "slinky-native"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.1.0" % Test

scalacOptions ++= {
  if (scalaJSVersion.startsWith("0.6.")) Seq("-P:scalajs:sjsDefinedByDefault")
  else Nil
}

Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

Test / jsEnv := new JSDOMNodeJSEnv(JSDOMNodeJSEnv.Config().withArgs(List("-r", "react-native-mock-render")))

def escapeBackslashes(path: String): String = {
  if (Properties.isWin)
    path.replace("\\", "\\\\")
  else
    path
}
