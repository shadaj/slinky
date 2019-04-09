enablePlugins(ScalaJSPlugin)

import org.scalajs.core.tools.io.{MemVirtualJSFile, VirtualJSFile}
import org.scalajs.jsenv.nodejs.NodeJSEnv

name := "slinky-native"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.8-RC2" % Test

scalacOptions += "-P:scalajs:sjsDefinedByDefault"

scalaJSModuleKind in Test := ModuleKind.CommonJSModule

jsEnv in Test := new NodeJSEnv() {
  override def customInitFiles(): Seq[VirtualJSFile] = super.customInitFiles() :+ new MemVirtualJSFile("addReactNativeMock.js").withContent(
    s"""require("${(baseDirectory.value / "node_modules/react-native-mock-render/mock.js").getAbsolutePath}");"""
  )
}
