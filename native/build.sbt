enablePlugins(ScalaJSPlugin)

import org.scalajs.core.tools.io.{MemVirtualJSFile, VirtualJSFile}
import org.scalajs.jsenv.nodejs.NodeJSEnv

import scala.util.Properties

name := "slinky-native"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8" % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalaJSModuleKind in Test := ModuleKind.CommonJSModule

jsEnv in Test := new NodeJSEnv() {
  override def customInitFiles(): Seq[VirtualJSFile] = super.customInitFiles() :+ new MemVirtualJSFile("addReactNativeMock.js").withContent(
    s"""require("${escapeBackslashes((baseDirectory.value / "node_modules/react-native-mock-render/mock.js").getAbsolutePath)}");"""
  )
}

def escapeBackslashes(path: String): String = {
  if (Properties.isWin)
    path.replace("\\", "\\\\")
  else
    path
}
