package slinky.native

import slinky.core.ReactComponentClass

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("react-native", "AppRegistry")
object AppRegistry extends js.Object {
  def registerComponent(appKey: String, componentProvider: js.Function0[ReactComponentClass[_]]): Unit = js.native
}
