import org.scalajs.core.tools.io.{MemVirtualJSFile, VirtualJSFile}
import org.scalajs.jsenv.nodejs.NodeJSEnv

enablePlugins(ScalaJSBundlerPlugin)

name := "slinky-native"

libraryDependencies += "org.scalatest" %%% "scalatest" % "3.0.3" % Test

npmDependencies in Test += "react" -> "16.2.0"
npmDependencies in Test += "react-native" -> "0.54.0"
npmDependencies in Test += "react-native-mock-render" -> "0.0.21"
npmDependencies in Test += "react-test-renderer" -> "16.2.0"

scalacOptions += "-P:scalajs:sjsDefinedByDefault"
scalacOptions += "-Ywarn-unused-import"

jsEnv in Test := new NodeJSEnv() {
  override def customInitFiles(): Seq[VirtualJSFile] = super.customInitFiles() :+ new MemVirtualJSFile("addReactNativeMock.js").withContent(
    s"""require("${(crossTarget.value / "scalajs-bundler/test/node_modules/react-native-mock-render/mock.js").getAbsolutePath}");"""
  )
}
