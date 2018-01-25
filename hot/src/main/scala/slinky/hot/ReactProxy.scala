package slinky.hot

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@JSImport("react-proxy", JSImport.Namespace, "ReactProxy")
@js.native
object ReactProxy extends js.Object {
  def createProxy(componentConstructor: js.Object): js.Object = js.native
  def getForceUpdate(react: js.Object): js.Function1[js.Object, Unit] = js.native
}
