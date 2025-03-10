enablePlugins(ScalaJSPlugin)

import org.scalajs.jsenv.nodejs.NodeJSEnv

import scala.util.Properties

name := "slinky-native"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.19" % Test

scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
Test / scalaJSLinkerConfig ~= { _.withESFeatures(_.withUseECMAScript2015(false)) }

Test / jsEnv := new NodeJSEnv(
  NodeJSEnv
    .Config()
    .withArgs(List("-r", baseDirectory.value.getAbsolutePath + "/node_modules/react-native-mock-render/mock.js"))
)

def escapeBackslashes(path: String): String =
  if (Properties.isWin)
    path.replace("\\", "\\\\")
  else
    path
